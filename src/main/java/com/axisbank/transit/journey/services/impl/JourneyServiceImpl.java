package com.axisbank.transit.journey.services.impl;

import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.model.DTO.BusRouteRedisDTO;
import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTO;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.service.BusStationService;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.DTO.GraphWeighted;
import com.axisbank.transit.core.model.DTO.NodeWeighted;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.DijkstrasAlgorithm;
import com.axisbank.transit.core.shared.utils.DistanceHelper;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.JourneyUtils;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTO;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.MAX_WALK_DISTANCE;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.NEARBY_STATION_RADIUS;
import static com.axisbank.transit.core.shared.constants.RedisConstants.*;
import static com.axisbank.transit.core.shared.utils.CommonUtils.addSecondsToTime;
import static com.axisbank.transit.core.shared.utils.CommonUtils.getTimeDiff;
import static com.axisbank.transit.journey.constants.JourneyTypes.*;


@Slf4j
@Service
public class JourneyServiceImpl implements JourneyService {

    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    StationService stationService;

    @Autowired
    TimeTableService timeTableService;

    @Autowired
    BusTimeTableService busTimeTableService;

    @Autowired
    BusStationService busStationService;

    @Autowired
    BusStationRepository busStationRepository;
    @Autowired
    RedisClient redisClient;
    @Autowired
    MetroStationRepository metroStationRepository;
    @Autowired
    OSRMUtils osrmUtils;
    @Autowired
    GlobalConfigService globalConfigService;

    Map<String, NodeWeighted> busWeightedGraph = new HashMap<>();
    Map<String, BusStation> busStationMap = new HashMap<>();
    Map<String, MetroStation> metroStationMap = new HashMap<>();
    Map<String, NodeWeighted> metroWeightedGraph = new HashMap<>();
    Map<String, Boolean> routeDataAvailable = new HashMap<>();
    double osrmCutoff;
    JourneyServiceImpl(){
        osrmCutoff = getMaxWalkDistGC();
    }

    @Override
    public List<JourneyPlannerRouteDTO> getRoutes(String source, String destination, String journeyType) throws ParseException {
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        return getRoutes(source, destination, currentTime, journeyType);
    }

    @Override
    public List<JourneyPlannerRouteDTO> getRoutes(String source, String destination, String startTime, String journeyType) throws ParseException {
        List<Double> sourceCoordinates = Arrays.stream(source.split(","))
                .map(Double::new)
                .collect(Collectors.toList());
        List<Double> destinationCoordinates = Arrays.stream(destination.split(","))
                .map(Double::new)
                .collect(Collectors.toList());
        double sourceLat = sourceCoordinates.get(0);
        double sourceLong = sourceCoordinates.get(1);
        double destLat = destinationCoordinates.get(0);
        double destLong = destinationCoordinates.get(1);
        List<JourneyPlannerRouteDTO> suggestedRoutes = new ArrayList<>();
        log.info("Getting journey between: {} and {}", source, destination);
        switch (journeyType) {
            case BUS:
                suggestedRoutes.addAll(getBusRoutes(sourceLat, sourceLong, destLat, destLong, startTime));
                break;
            case METRO:
                suggestedRoutes.addAll(getMetroRoutes(sourceLat, sourceLong, destLat, destLong, startTime));
                break;
            default:
                suggestedRoutes.addAll(getSuggestedRoutes(sourceLat, sourceLong, destLat, destLong, startTime));
                break;
        }
        return suggestedRoutes;
    }

    @Override
    public List<BusSrcDestRouteDTO> getSuggestedRoutesGraph(String sourceStationId, String destinationStationId) throws JsonProcessingException {
        String redisKey = "suggested_route:" + sourceStationId + "-" + destinationStationId;
        try {
            String routeObj = redisClient.getValue(redisKey);
            if (routeObj != null || !routeObj.equals("")) {
                BusRouteRedisDTO busRoute = CommonUtils.convertJsonStringToObject(routeObj, BusRouteRedisDTO.class);
                return busRoute.getBusSrcDestRouteDTOList();
            }
        } catch (Exception ex) {
            log.error("Failed to parse Object From Redis");
        }
        String routeGraph;
        try {
            routeGraph = redisClient.getValue(REDIS_SUGGESTED_ROUTE_GRAPH);
        } catch (Exception ex) {
            log.error("Failed to parse Object From Redis");
            routeGraph = setSuggestedGraph();
        }
        BusRouteRedisDTO bsr = new BusRouteRedisDTO();
        GraphWeighted nodes = CommonUtils.convertJsonStringToObject(routeGraph, GraphWeighted.class);
        DijkstrasAlgorithm djk = new DijkstrasAlgorithm(nodes.getNodes());
        List<BusSrcDestRouteDTO> busSrcDestRouteDTOS = djk.DijkstraShortestPath(sourceStationId, destinationStationId);
        bsr.setSource(sourceStationId);
        bsr.setDestination(destinationStationId);
        bsr.setBusSrcDestRouteDTOList(busSrcDestRouteDTOS);
        try {
            String redisString = CommonUtils.convertObjectToJsonString(bsr);
            redisClient.setValue(redisKey, redisString, 60 * 60 * 24 * 5);
        } catch (Exception ex) {
            log.error("Failed to store Data in redis: {}", ex.getMessage());
        }
        return busSrcDestRouteDTOS;
    }

    @Override
    public Double getNearByStationRadiusGC() {
        // Default nearBy station Radius
        double dist = 2000;
        try{
            GlobalConfigDTO configDTO = globalConfigService.getGlobalConfig(NEARBY_STATION_RADIUS, false);
            if (configDTO!=null){
                log.info("Fetch nearby station radius from db");
                dist = Integer.parseInt(configDTO.getValue());
            }
        } catch (Exception ex){
            log.error("failed to nearby station radius using default.., Exception: {}",ex.getMessage());
        }
        return dist;
    }

    @Override
    public Double getMaxWalkDistGC() {
        // Default Max walk distance
        double dist = 2000;
        try{
            GlobalConfigDTO configDTO = globalConfigService.getGlobalConfig(MAX_WALK_DISTANCE, false);
            if (configDTO!=null){
                log.info("Fetch max walk distance from db");
                dist = Integer.parseInt(configDTO.getValue());
            }
        } catch (Exception ex){
            log.error("failed to get max walk distance using default, Exception: {}",ex.getMessage());
        }
        return dist;
    }

    @Override
    public String setBusRoutesGraph() throws JsonProcessingException {
        String data = CommonUtils.convertObjectToJsonString(getBusGraph());
        try {
            redisClient.setValue(REDIS_BUS_ROUTE_GRAPH, data);
        } catch (Exception ex) {
            log.error("Failed to store Data in redis: {}", ex.getMessage());
        }
        return data;
    }

    @Override
    public String setMetroRoutesGraph() throws JsonProcessingException {
        String data = CommonUtils.convertObjectToJsonString(getMetroGraph());
        try {
            redisClient.setValue(REDIS_METRO_ROUTE_GRAPH, data);
        } catch (Exception ex) {
            log.error("Failed to store Data in redis: {}", ex.getMessage());
        }
        return data;
    }

    @Override
    public String setSuggestedGraph() throws JsonProcessingException {
        if(metroWeightedGraph.isEmpty())
            setMetroRoutesGraph();
        if(busWeightedGraph.isEmpty())
            setBusRoutesGraph();
        String data = CommonUtils.convertObjectToJsonString(getSuggestedGraph());
        try {
            redisClient.setValue(REDIS_SUGGESTED_ROUTE_GRAPH, data);
        } catch (Exception ex) {
            log.error("Failed to store Data in redis: {}", ex.getMessage());
        }
        return data;
    }


    private GraphWeighted getBusGraph(){
        List<BusStation> busStations = busStationRepository.findAll();
        GraphWeighted graphWeighted = new GraphWeighted(true);
        int seq = 0;
        for (BusStation busStation : busStations) {
            NodeWeighted node = new NodeWeighted(seq, busStation.getStationId());
            seq++;
            busWeightedGraph.put(busStation.getStationId(), node);
            busStationMap.put(busStation.getStationId(), busStation);
        }

        busWeightedGraph.forEach((k, v) -> {
            NodeWeighted source = busWeightedGraph.get(k);
            BusStation sourceStation = busStationMap.get(k);
            Set<BusRoute> busRoutes = sourceStation.getRouteSet();
            CoordinatesDto srcCoordinatesDto= new CoordinatesDto(sourceStation.getLatitude(), sourceStation.getLongitude());
            for (BusRoute br : busRoutes) {
                Set<BusStation> allStations = br.getBusStationSet();
                String routeId = br.getRouteCode();
                // Lowest priority to build graph so weitght starts after 50
                int initialWeight = 50;
                String busType = br.getBusType();
                for (BusStation bstation : allStations) {
                    if (k.equals(bstation.getStationId())) continue;
                    CoordinatesDto destCoordinatesDto= new CoordinatesDto(bstation.getLatitude(),bstation.getLongitude());
                    // Add initial weight to total distance to give metro route priority
                    double totalWeight = DistanceHelper.getHaversine(srcCoordinatesDto, destCoordinatesDto)+initialWeight;

                    // Give absolute priority to feeder bus
                    if(busType.equalsIgnoreCase("feeder"))
                        totalWeight=50;
                    NodeWeighted dest = busWeightedGraph.get(bstation.getStationId());
                    graphWeighted.addEdge(source, dest, totalWeight, routeId);
                    initialWeight++;
                }
            }
        });
        return graphWeighted;
    }
    private GraphWeighted getMetroGraph() {
        List<MetroStation> metroStations = metroStationRepository.findAll();
        Map<String, Double> stationDist = new HashMap<>();
        GraphWeighted graphWeighted = new GraphWeighted(true);
        int seq = 0;
        for (MetroStation metroStation : metroStations) {
            NodeWeighted node = new NodeWeighted(seq, metroStation.getStationId());
            seq++;
            stationDist.put(metroStation.getStationId(), metroStation.getDistance());
            metroWeightedGraph.put(metroStation.getStationId(), node);
            metroStationMap.put(metroStation.getStationId(), metroStation);
        }
        metroWeightedGraph.forEach((k, v) -> {
            NodeWeighted source = metroWeightedGraph.get(k);
            for (MetroStation mStation : metroStations) {
                if (k.equals(mStation.getStationId())) continue;
                double sourceDist = stationDist.get(source.getName());
                double destDist = stationDist.get(mStation.getStationId());
                double totalWeight = 0;
                if(sourceDist>destDist){
                    totalWeight = sourceDist-destDist;
                } else {
                    totalWeight = destDist-sourceDist;
                }
                NodeWeighted dest = metroWeightedGraph.get(mStation.getStationId());
                graphWeighted.addEdge(source, dest, totalWeight, "METRO");
            }
        });
        return graphWeighted;
    }

    private GraphWeighted getSuggestedGraph() {
        GraphWeighted busGraph = getBusGraph();
        GraphWeighted metroGraph = getMetroGraph();
        Set<NodeWeighted> nodes = busGraph.getNodes();
        nodes.addAll(metroGraph.getNodes());
        GraphWeighted graphWeighted = new GraphWeighted(nodes, true, busGraph.getEdgeCount()
                +metroGraph.getEdgeCount());
        metroWeightedGraph.forEach((k, v) -> {
            NodeWeighted source = metroWeightedGraph.get(k);
            MetroStation metroStation = metroStationMap.get(k);

            // medium priority to build graph so weitght starts after 40
            int initialWeight = 40;
            List<NearByBusStationsDTO> busStations = busStationService.getNearByBusStationSQL(metroStation.getLatitude(), metroStation.getLongitude(),
                    getNearByStationRadiusGC());
            for (NearByBusStationsDTO nStation : busStations) {
                if (k.equals(nStation.getStationId())) continue;
                NodeWeighted dest = busWeightedGraph.get(nStation.getStationId());
                // We add initial weight to walking distance to bus so that we have more priority with metro
                graphWeighted.addEdge(source, dest, nStation.getSqlDist()+initialWeight, "WALK_BUS");
                graphWeighted.addEdge(dest, source, nStation.getSqlDist(), "WALK_METRO");
                initialWeight++;
            }
        });
        return graphWeighted;
    }

    private List<NearByStationsDTO> filterNearBystationsOSRM(List<NearByStationsDTO> nearByStationsDTOS, double osrmCutoff) {
        nearByStationsDTOS.removeIf(p -> p.getOsrmDistance() > osrmCutoff);
        return nearByStationsDTOS;
    }

    private List<NearByBusStationsDTO> filterNearByBusStationsOSRM(List<NearByBusStationsDTO> nearByStationsDTOS, double osrmCutoff) {
        nearByStationsDTOS.removeIf(p -> p.getOsrmDistance() > osrmCutoff);
        return nearByStationsDTOS;
    }

    private List<JourneyPlannerRouteDTO> getBusRoutes(double sourceLat, double sourceLong, double destLat,
                                                      double destLong, String startTime) throws ParseException {
        List<NearByBusStationsDTO> nearBySourceBusStationsDTOList = filterNearByBusStationsOSRM(busStationService.getNearbyStations(
                sourceLat, sourceLong), osrmCutoff);
        List<NearByBusStationsDTO> nearByDestinationBusStationsDTOList = filterNearByBusStationsOSRM(busStationService.getNearbyStations(
                destLat, destLong), osrmCutoff);
        return getBusRouteDij(sourceLat, sourceLong, destLat, destLong, Time.valueOf(startTime),
                nearBySourceBusStationsDTOList, nearByDestinationBusStationsDTOList);
    }

    private List<JourneyPlannerRouteDTO> getMetroRoutes(double sourceLat, double sourceLong, double destLat,
                                                      double destLong, String startTime) throws ParseException {
        List<NearByStationsDTO> nearBySourceStationsDTOList = filterNearBystationsOSRM(stationService.getNearbyStations(
                sourceLat, sourceLong), osrmCutoff);
        List<NearByStationsDTO> nearByDestinationStationsDTOList = filterNearBystationsOSRM(stationService.getNearbyStations(
                destLat, destLong), osrmCutoff);
        return journeyMetroRoute(sourceLat, sourceLong, destLat, destLong, Time.valueOf(startTime),
                nearBySourceStationsDTOList, nearByDestinationStationsDTOList);
    }
    private List<JourneyPlannerRouteDTO> getSuggestedRoutes(double sourceLat, double sourceLong, double destLat,
                                                            double destLong, String startTime) throws ParseException{

        List<NearByStationsDTO> nearBySourceStationsDTOList = filterNearBystationsOSRM(stationService.getNearbyStations(
                sourceLat, sourceLong), osrmCutoff);
        List<NearByStationsDTO> nearByDestinationStationsDTOList = filterNearBystationsOSRM(stationService.getNearbyStations(
                destLat, destLong), osrmCutoff);
        List<JourneyPlannerRouteDTO> jpr = journeyMetroRoute(sourceLat, sourceLong, destLat, destLong, Time.valueOf(startTime),
                nearBySourceStationsDTOList, nearByDestinationStationsDTOList);
        if(!jpr.isEmpty())
            return jpr;
        List<NearByBusStationsDTO> nearBySourceBusStationsDTOList = filterNearByBusStationsOSRM(busStationService.getNearbyStations(
                sourceLat, sourceLong), osrmCutoff);
        List<NearByBusStationsDTO> nearByDestinationBusStationsDTOList = filterNearByBusStationsOSRM(busStationService.getNearbyStations(
                destLat, destLong), osrmCutoff);
        List<JourneyPlannerRouteDTO> busJourneyPlan = getBusRouteDij(sourceLat, sourceLong, destLat, destLong, Time.valueOf(startTime),
                nearBySourceBusStationsDTOList, nearByDestinationBusStationsDTOList);


        List<NearByStationsDTO> nearBySourceList = new ArrayList<>(nearBySourceStationsDTOList);
        for(NearByBusStationsDTO nearByBusStationsDTO: nearBySourceBusStationsDTOList){
            nearBySourceList.add(modelMapper.map(nearByBusStationsDTO, NearByStationsDTO.class));
        }
        List<NearByStationsDTO> nearBydestList = new ArrayList<>(nearByDestinationStationsDTOList);
        for(NearByBusStationsDTO nearByBusStationsDTO: nearByDestinationBusStationsDTOList){
            nearBydestList.add(modelMapper.map(nearByBusStationsDTO, NearByStationsDTO.class));
        }

        jpr.addAll(journeySuggestedRoute(sourceLat, sourceLong, destLat, destLong, Time.valueOf(startTime),
                nearBySourceList, nearBydestList));
        jpr.addAll(busJourneyPlan);
        return jpr;
    }
    private List<JourneyPlannerRouteDTO> journeySuggestedRoute(Double sourceLat, Double sourceLong,
                                                           Double destinationLat, Double destinationLong, Time startTime,
                                                           List<NearByStationsDTO> sources,
                                                           List<NearByStationsDTO> destinations) throws ParseException {
        SortedSet<JourneyPlannerRouteDTO> journeyRoutes =
                new TreeSet<>(Comparator.comparing(JourneyPlannerRouteDTO::getTotalDuration));
        log.info("Received: {} nearby source and {} nearby destination", sources.size(), destinations.size());
        for (NearByStationsDTO nbs : sources) {
            for (NearByStationsDTO nbd : destinations) {
                if (nbs.getStationId().equals(nbd.getStationId())) continue;
                Boolean hasRoute = routeDataAvailable.get(nbs.getStationId()+"-"+nbd.getStationId());
                if(hasRoute!=null && hasRoute)
                    continue;
                JourneyPlannerRouteDTO journeyPlannerRouteDTO = new JourneyPlannerRouteDTO();
                JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
                journeyModeDetails.setType(WALK);
                journeyModeDetails.setDistance(nbs.getOsrmDistance());
                journeyModeDetails.setSourceLatitude(sourceLat);
                journeyModeDetails.setSourceLongitude(sourceLong);
                journeyModeDetails.setDestinationLatitude(nbs.getLatitude());
                journeyModeDetails.setDestinationLongitude(nbs.getLongitude());
                journeyModeDetails.setTime(startTime);
                journeyModeDetails.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(startTime.toString(),
                        nbs.getDuration().intValue())));
                journeyModeDetails.setTravelTime(nbs.getDuration());
                SortedSet<JourneyModeDetails> journeyModes = getSuggestedRouteDijktras(nbs.getStationId(),
                        nbd.getStationId(), journeyModeDetails.getEstimatedArrivalTime().toString());
                if (journeyModes == null || journeyModes.size() < 1) continue;
                journeyModes.add(journeyModeDetails);
                JourneyModeDetails lstBustRoute = journeyModes.last();
                JourneyModeDetails journeyModeDetailsDest = new JourneyModeDetails();
                journeyModeDetailsDest.setType(WALK);
                journeyModeDetailsDest.setDistance(nbd.getOsrmDistance());
                journeyModeDetailsDest.setSourceLatitude(lstBustRoute.getDestinationLatitude());
                journeyModeDetailsDest.setSourceLongitude(lstBustRoute.getDestinationLongitude());
                journeyModeDetailsDest.setDestinationLatitude(destinationLat);
                journeyModeDetailsDest.setDestinationLongitude(destinationLong);
                journeyModeDetailsDest.setTime(lstBustRoute.getEstimatedArrivalTime());
                journeyModeDetailsDest.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(
                        lstBustRoute.getEstimatedArrivalTime().toString(), nbd.getDuration().intValue())));
                journeyModeDetailsDest.setTravelTime(nbd.getDuration());
                journeyModes.add(journeyModeDetailsDest);

                double totalDistance = journeyModes.stream().mapToDouble(JourneyModeDetails::getDistance).sum();
                long totalDuration = getTimeDiff(journeyModeDetailsDest.getEstimatedArrivalTime(), startTime);
                double totalFare = journeyModes.stream().mapToDouble(JourneyModeDetails::getFare).sum();
                journeyPlannerRouteDTO.setAmount(totalFare);
                journeyPlannerRouteDTO.setArrivalTime(journeyModeDetailsDest.getEstimatedArrivalTime());
                journeyPlannerRouteDTO.setDepartureTime(startTime);
                journeyPlannerRouteDTO.setTotalDistance(totalDistance);
                journeyPlannerRouteDTO.setTotalDuration(totalDuration);
                journeyPlannerRouteDTO.setJourneyModeDetails(new ArrayList<>(journeyModes));
                journeyRoutes.add(journeyPlannerRouteDTO);
            }
        }
        return new ArrayList<>(journeyRoutes);
    }

    private List<JourneyPlannerRouteDTO> journeyMetroRoute(Double sourceLat, Double sourceLong,
                                                           Double destinationLat, Double destinationLong, Time startTime,
                                                           List<NearByStationsDTO> sourceMetros,
                                                           List<NearByStationsDTO> destinationMetros) throws ParseException {
        SortedSet<JourneyPlannerRouteDTO> journeyRoutes =
                new TreeSet<>(Comparator.comparing(JourneyPlannerRouteDTO::getTotalDuration));
        for (NearByStationsDTO nbs : sourceMetros) {
            for (NearByStationsDTO nbd : destinationMetros) {
                JourneyPlannerRouteDTO journeyPlannerRouteDTO = new JourneyPlannerRouteDTO();
                JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
                journeyModeDetails.setType(WALK);
                journeyModeDetails.setDistance(nbs.getOsrmDistance());
                journeyModeDetails.setSourceLatitude(sourceLat);
                journeyModeDetails.setSourceLongitude(sourceLong);
                journeyModeDetails.setDestinationLatitude(nbs.getLatitude());
                journeyModeDetails.setDestinationLongitude(nbs.getLongitude());
                journeyModeDetails.setTime(startTime);
                journeyModeDetails.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(startTime.toString(),
                        nbs.getDuration().intValue())));
                journeyModeDetails.setTravelTime(nbs.getDuration());

                JourneyModeDetails journeyModeDetailsMetro = timeTableService.getMetroRouteDetails(nbs.getStationId(),
                        nbd.getStationId(), journeyModeDetails.getEstimatedArrivalTime().toString());
                if (journeyModeDetailsMetro == null) continue;
                List<JourneyModeDetails> journeyModes = new ArrayList<>();
                journeyModes.add(journeyModeDetails);
                journeyModes.add(journeyModeDetailsMetro);
                JourneyModeDetails journeyModeDetailsDest = new JourneyModeDetails();
                journeyModeDetailsDest.setType(WALK);
                journeyModeDetailsDest.setDistance(nbd.getOsrmDistance());
                journeyModeDetailsDest.setSourceLatitude(journeyModeDetailsMetro.getDestinationLatitude());
                journeyModeDetailsDest.setSourceLongitude(journeyModeDetailsMetro.getDestinationLongitude());
                journeyModeDetailsDest.setDestinationLatitude(destinationLat);
                journeyModeDetailsDest.setDestinationLongitude(destinationLong);
                journeyModeDetailsDest.setTime(journeyModeDetailsMetro.getEstimatedArrivalTime());
                journeyModeDetailsDest.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(
                        journeyModeDetailsMetro.getEstimatedArrivalTime().toString(), nbd.getDuration().intValue())));
                journeyModeDetailsDest.setTravelTime(nbd.getDuration());
                journeyModes.add(journeyModeDetailsDest);

                Double totalDistance = journeyModeDetails.getDistance() + journeyModeDetailsMetro.getDistance()
                        + journeyModeDetailsDest.getDistance();
                long totalDuration = getTimeDiff(journeyModeDetailsDest.getEstimatedArrivalTime(), startTime);

                journeyPlannerRouteDTO.setAmount(journeyModeDetailsMetro.getFare());
                journeyPlannerRouteDTO.setArrivalTime(journeyModeDetailsDest.getEstimatedArrivalTime());
                journeyPlannerRouteDTO.setDepartureTime(startTime);
                journeyPlannerRouteDTO.setTotalDistance(totalDistance);
                journeyPlannerRouteDTO.setTotalDuration(totalDuration);
                journeyPlannerRouteDTO.setJourneyModeDetails(journeyModes);
                journeyRoutes.add(journeyPlannerRouteDTO);
                routeDataAvailable.put(nbs.getStationId()+"-"+nbd.getStationId(), true);
            }
        }
        return new ArrayList<>(journeyRoutes);
    }


    private List<JourneyPlannerRouteDTO> getBusRouteDij(Double sourceLat, Double sourceLong,
                                                        Double destinationLat, Double destinationLong, Time startTime,
                                                        List<NearByBusStationsDTO> sourceMetros,
                                                        List<NearByBusStationsDTO> destinationMetros) throws ParseException {
        SortedSet<JourneyPlannerRouteDTO> journeyRoutes =
                new TreeSet<>(Comparator.comparing(JourneyPlannerRouteDTO::getTotalDuration));
        for (NearByBusStationsDTO nbs : sourceMetros) {
            for (NearByBusStationsDTO nbd : destinationMetros) {
                if (nbs.getStationId().equals(nbd.getStationId())) continue;
                JourneyPlannerRouteDTO journeyPlannerRouteDTO = new JourneyPlannerRouteDTO();
                JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
                journeyModeDetails.setType(WALK);
                journeyModeDetails.setDistance(nbs.getOsrmDistance());
                journeyModeDetails.setSourceLatitude(sourceLat);
                journeyModeDetails.setSourceLongitude(sourceLong);
                journeyModeDetails.setDestinationLatitude(nbs.getLatitude());
                journeyModeDetails.setDestinationLongitude(nbs.getLongitude());
                journeyModeDetails.setTime(startTime);
                journeyModeDetails.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(startTime.toString(),
                        nbs.getDuration().intValue())));
                journeyModeDetails.setTravelTime(nbs.getDuration());
                SortedSet<JourneyModeDetails> journeyModes = busTimeTableService.getRouteDijktras(nbs.getStationId(),
                        nbd.getStationId(), journeyModeDetails.getEstimatedArrivalTime().toString());
                if (journeyModes == null || journeyModes.size() < 1) continue;
                journeyModes.add(journeyModeDetails);
                JourneyModeDetails lstBustRoute = journeyModes.last();
                JourneyModeDetails journeyModeDetailsDest = new JourneyModeDetails();
                journeyModeDetailsDest.setType(WALK);
                journeyModeDetailsDest.setDistance(nbd.getOsrmDistance());
                journeyModeDetailsDest.setSourceLatitude(lstBustRoute.getDestinationLatitude());
                journeyModeDetailsDest.setSourceLongitude(lstBustRoute.getDestinationLongitude());
                journeyModeDetailsDest.setDestinationLatitude(destinationLat);
                journeyModeDetailsDest.setDestinationLongitude(destinationLong);
                journeyModeDetailsDest.setTime(lstBustRoute.getEstimatedArrivalTime());
                journeyModeDetailsDest.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(
                        lstBustRoute.getEstimatedArrivalTime().toString(), nbd.getDuration().intValue())));
                journeyModeDetailsDest.setTravelTime(nbd.getDuration());
                journeyModes.add(journeyModeDetailsDest);

                double totalDistance = journeyModes.stream().mapToDouble(JourneyModeDetails::getDistance).sum();
                long totalDuration = getTimeDiff(journeyModeDetailsDest.getEstimatedArrivalTime(), startTime);
                double totalFare = journeyModes.stream().mapToDouble(JourneyModeDetails::getFare).sum();
                journeyPlannerRouteDTO.setAmount(totalFare);
                journeyPlannerRouteDTO.setArrivalTime(journeyModeDetailsDest.getEstimatedArrivalTime());
                journeyPlannerRouteDTO.setDepartureTime(startTime);
                journeyPlannerRouteDTO.setTotalDistance(totalDistance);
                journeyPlannerRouteDTO.setTotalDuration(totalDuration);
                journeyPlannerRouteDTO.setJourneyModeDetails(new ArrayList<>(journeyModes));
                journeyRoutes.add(journeyPlannerRouteDTO);
                routeDataAvailable.put(nbs.getStationId()+"-"+nbd.getStationId(), true);
            }
        }
        return new ArrayList<>(journeyRoutes);
    }


    public SortedSet<JourneyModeDetails> getSuggestedRouteDijktras(String sourceStationId, String destinationStationId, String startTime) throws ParseException {
        List<BusSrcDestRouteDTO> shortestRoute;
        try {
            shortestRoute = getSuggestedRoutesGraph(sourceStationId, destinationStationId);
            log.info("SuggestedRoutes: {}", CommonUtils.convertObjectToJsonString(shortestRoute));
        } catch (Exception ex) {
            log.error("Exception while getting routes:{}", ex.getMessage());
            return null;
        }
        if (shortestRoute == null) return null;
        if (shortestRoute.size() < 1) return null;
        String stTime = startTime;
        SortedSet<JourneyModeDetails> getRouteJourney =
                new TreeSet<>(Comparator.comparing(JourneyModeDetails::getTime));
        for (BusSrcDestRouteDTO listRoute : shortestRoute) {
            String routeCode = listRoute.getRouteCode();
            JourneyModeDetails journeyModeDetails = null;
            if(!routeCode.equalsIgnoreCase("METRO") && !routeCode.startsWith("WALK")) {
                journeyModeDetails =  busTimeTableService.getJourneyDetailsByRouteCode(listRoute.getSourceStation(),
                        listRoute.getDestinationStation(), routeCode, stTime);
            } else if(routeCode.equalsIgnoreCase("METRO")){
                journeyModeDetails = timeTableService.getMetroRouteDetails(listRoute.getSourceStation(),
                        listRoute.getDestinationStation(), stTime);
            } else {
                journeyModeDetails=calculateWalkJourney(listRoute.getSourceStation(),
                        listRoute.getDestinationStation(),routeCode, stTime);
            }
            if (journeyModeDetails != null) {
                getRouteJourney.add(journeyModeDetails);
                stTime = journeyModeDetails.getEstimatedArrivalTime().toString();
            }
        }
        try{
            log.info("JourneyDetails: {}", CommonUtils.convertObjectToJsonString(getRouteJourney));
        } catch (Exception ex){
            log.error("Exception: {}", ex.getMessage());
        }
        if ((getRouteJourney.size()>0) && (getRouteJourney.first().getType().equalsIgnoreCase("Walk") || getRouteJourney.last().getType().equalsIgnoreCase("Walk")))
                return null;
        return getRouteJourney;
    }

    private JourneyModeDetails calculateWalkJourney(String sourceId, String destId, String routeCode, String startTime) throws ParseException {
        MetroStationDTO metroStationDTO;
        BusStation busStation;
        CoordinatesDto srcCoordinatesDto;
        CoordinatesDto destCoordinateDto;
        Double dist=0.0;
        Double duration=0.0;
        switch (routeCode){
            case "WALK_BUS":
                metroStationDTO = stationService.getStation(sourceId);
                busStation = busStationRepository.findByStationId(destId);
                srcCoordinatesDto = new CoordinatesDto(metroStationDTO.getLatitude(), metroStationDTO.getLongitude());
                destCoordinateDto = new CoordinatesDto(busStation.getLatitude(), busStation.getLongitude());
                break;
            case "WALK_METRO":
                metroStationDTO = stationService.getStation(destId);
                busStation = busStationRepository.findByStationId(sourceId);
                srcCoordinatesDto= new CoordinatesDto(busStation.getLatitude(), busStation.getLongitude());
                destCoordinateDto = new CoordinatesDto(metroStationDTO.getLatitude(), metroStationDTO.getLongitude());
                break;
            default:
                return null;
        }
        List<CoordinatesDto> dest= new ArrayList<>();
        dest.add(destCoordinateDto);
        List<Map<String,Double>> distances = osrmUtils.getOSRMDistance(srcCoordinatesDto, dest);
        if(distances.size()>0){
            dist = distances.get(0).get("distance");
            duration = JourneyUtils.getDurationFromDistance(dist);
        }
        return getWalkJourney(srcCoordinatesDto.getLatitude(), srcCoordinatesDto.getLongitude(),
                destCoordinateDto.getLatitude(), destCoordinateDto.getLongitude(), dist, Time.valueOf(startTime), duration);
    }

    private JourneyModeDetails getWalkJourney(Double sourceLat, Double sourceLon, Double destLat, Double destLon,
                                              Double dist, Time startTime, Double duration) throws ParseException {
        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(WALK);
        journeyModeDetails.setDistance(dist);
        journeyModeDetails.setSourceLatitude(sourceLat);
        journeyModeDetails.setSourceLongitude(sourceLon);
        journeyModeDetails.setDestinationLatitude(destLat);
        journeyModeDetails.setDestinationLongitude(destLon);
        journeyModeDetails.setTime(startTime);
        journeyModeDetails.setEstimatedArrivalTime(Time.valueOf(addSecondsToTime(startTime.toString(),
               duration.intValue())));
        journeyModeDetails.setTravelTime(duration);
        return journeyModeDetails;
    }
}
