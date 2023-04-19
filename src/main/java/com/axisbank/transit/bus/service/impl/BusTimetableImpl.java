package com.axisbank.transit.bus.service.impl;

import com.axisbank.transit.bus.model.DAO.BusTimeTable;
import com.axisbank.transit.bus.model.DTO.BusDeparturesDTO;
import com.axisbank.transit.bus.model.DTO.BusRouteRedisDTO;
import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.bus.model.DTO.BusTimeTableFareInterface;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.repository.BusTimeTableRepository;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.model.DTO.GraphWeighted;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.DijkstrasAlgorithm;
import com.axisbank.transit.core.shared.utils.DistanceHelper;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.RedisConstants.REDIS_BUS_ROUTE_GRAPH;
import static com.axisbank.transit.core.shared.utils.CommonUtils.getTimeDiff;
import static com.axisbank.transit.journey.constants.JourneyTypes.BUS;

@Slf4j
@Service
public class BusTimetableImpl implements BusTimeTableService {
    private final ModelMapper modelMapper = new ModelMapper();
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    BusTimeTableRepository busTimeTableRepository;
    @Autowired
    BusStationRepository busStationRepository;
    @Autowired
    RedisClient redisClient;
    @Autowired
    JourneyService journeyService;
    @Autowired
    OSRMUtils osrmUtils;

    @Override
    public List<BusSrcDestRouteDTO> getBusRoutesGraph(String sourceStationId, String destinationStationId) throws JsonProcessingException {
        String redisKey = "bus_route:" + sourceStationId + "-" + destinationStationId;
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
            routeGraph = redisClient.getValue(REDIS_BUS_ROUTE_GRAPH);
        } catch (Exception ex) {
            log.error("Failed to parse Object From Redis");
            routeGraph = journeyService.setBusRoutesGraph();
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
    public List<BusDeparturesDTO> getUpcomingDepartures(String sourceStationId) {
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");

        List<BusTimeTable> busTimeTables = busTimeTableRepository.
                findAllByBusStation_StationIdAndArivalTimeGreaterThanOrderByArivalTimeAsc(sourceStationId,
                        Time.valueOf(currentTime));
        SortedSet<BusDeparturesDTO> busDeparturesDTOS =
                new TreeSet<>(Comparator.comparing(BusDeparturesDTO::getArivalTime));
        for (BusTimeTable bst : busTimeTables) {
            BusDeparturesDTO bsd = modelMapper.map(bst, BusDeparturesDTO.class);
            bsd.setRouteName(bst.getBusRoute().getRouteName());
            busDeparturesDTOS.add(bsd);
        }
        return new ArrayList<>(busDeparturesDTOS);
    }

    @Override
    public SortedSet<JourneyModeDetails> getRouteDijktras(String sourceStationId, String destinationStationId) {
        String startTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        return getRouteDijktras(sourceStationId, destinationStationId, startTime);
    }

    @Override
    public SortedSet<JourneyModeDetails> getRouteDijktras(String sourceStationId, String destinationStationId, String startTime) {
        List<BusSrcDestRouteDTO> shortestRoute;
        try {
            shortestRoute = getBusRoutesGraph(sourceStationId, destinationStationId);
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
            JourneyModeDetails journeyModeDetails = getJourneyDetailsByRouteCode(listRoute.getSourceStation(),
                    listRoute.getDestinationStation(), listRoute.getRouteCode(), stTime);
            if (journeyModeDetails != null) {
                getRouteJourney.add(journeyModeDetails);
                stTime = journeyModeDetails.getEstimatedArrivalTime().toString();
            }
        }
        return getRouteJourney;
    }

    public List<Time> getUpcomingTimings(String sourceStationId, String destinationStationId) {
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        return getUpcomingTimings(sourceStationId, destinationStationId, currentTime);
    }

    @Override
    public List<Time> getUpcomingTimings(String sourceStationId, String destinationStationId, String startTime) {
        String currentTime = "1970-01-01 " + startTime;

        List<BusTimeTableFareInterface> upcomingTrips = busTimeTableRepository.getMetroRouteStationsTimetableAndFare(
                sourceStationId, destinationStationId, "%", currentTime
        );
        return upcomingTrips.stream().map(s -> Time.valueOf(s.getSource_Arival())).collect(Collectors.toList());
    }

    public List<Time> getUpcomingStationTimings(String sourceStationId, String destinationStationId, String startTime) {
        List<Time> allData = new ArrayList<>();
        String redisKey = "bus_upcoming:"+sourceStationId+"-"+destinationStationId;
        try{
            String times = redisClient.getValue(redisKey);
            allData = mapper.readValue(times, new TypeReference<List<Time>>(){});
        } catch (Exception ex){
            log.error("Failed to get timings from redis:{}", ex.getMessage());
        }
        if(allData.isEmpty()){
            List<BusTimeTableFareInterface> upcomingTrips = busTimeTableRepository.getMetroRouteStationsTimetableAndFare(
                    sourceStationId, destinationStationId, "%", "1970-01-01 00:00:00"
            );
            allData = upcomingTrips.stream().map(s -> Time.valueOf(s.getSource_Arival())).collect(Collectors.toList());
            try{
                redisClient.setValue(redisKey, CommonUtils.convertObjectToJsonString(allData), 6*60*60);
            } catch (Exception ex){
                log.error("Failed to store upcoming data");
            }
        }
        allData.removeIf(p -> p.getTime() < Time.valueOf(startTime).getTime());
        return allData;
    }

    @Override
    public JourneyModeDetails getJourneyDetailsByRouteCode(String sourceStationId, String destinationStationId,
                                                            String routeCode, String startTime) {

        String redisKey = "bus_route:"+sourceStationId+"-"+destinationStationId;
        try{
            String journeyData = redisClient.getValue(redisKey);
            JourneyModeDetails journeyDetails= CommonUtils.convertJsonStringToObject(journeyData, JourneyModeDetails.class);
            List<Time> upcomingTimings = getUpcomingStationTimings(sourceStationId, destinationStationId, startTime);
            if (upcomingTimings.isEmpty())
                return null;
            journeyDetails.setTimings(upcomingTimings);
            journeyDetails.setTime(upcomingTimings.get(0));
            journeyDetails.setEstimatedArrivalTime(Time.valueOf(CommonUtils.addSecondsToTime(upcomingTimings.get(0).toString(), journeyDetails.getTravelTime().intValue())));
            log.info("Sending bus data from cache");
            return journeyDetails;
        } catch(Exception ex) {
            log.error("Failed to get bus journey cache data");
        }
        startTime = "1970-01-01 " + startTime;
        log.info("---------BusRequest: sourceId:{} destId:{} StartTime:{}", sourceStationId, destinationStationId, startTime);
        List<BusTimeTableFareInterface> upcomingTrips = busTimeTableRepository.getMetroRouteStationsTimetableAndFare(
                sourceStationId, destinationStationId, "%", startTime
        );
        log.info("----------Upcoming TripSize:{}", upcomingTrips.size());
        if (upcomingTrips.size() < 1) return null;
        BusTimeTableFareInterface latestTrip = upcomingTrips.get(0);
        try{
            log.info("BusUpcomingData:{}", CommonUtils.convertObjectToJsonString(latestTrip));
        } catch (Exception ex){
            log.error("Failed to log data: {}", ex.getMessage());
        }
        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(BUS);
        if (latestTrip.getBus_Type().equalsIgnoreCase("feeder"))
            journeyModeDetails.setType("feeder");
        CoordinatesDto srcCoordinatesDto= new CoordinatesDto(latestTrip.getSource_Latitude(), latestTrip.getSource_Longitude());
        CoordinatesDto destCoordinateDto = new CoordinatesDto(latestTrip.getDestination_Latitude(), latestTrip.getDestination_Longitude());
        List<CoordinatesDto> dest = Collections.singletonList(destCoordinateDto);
        journeyModeDetails.setDistance(getBusStationDist(sourceStationId, destinationStationId, srcCoordinatesDto,dest));
        journeyModeDetails.setSource(latestTrip.getSource_Display_Name());
        journeyModeDetails.setSourceLatitude(latestTrip.getSource_Latitude());
        journeyModeDetails.setSourceLongitude(latestTrip.getSource_Longitude());
        journeyModeDetails.setSourceId(sourceStationId);

        journeyModeDetails.setDestination(latestTrip.getDestination_Display_Name());
        journeyModeDetails.setDestinationLatitude(latestTrip.getDestination_Latitude());
        journeyModeDetails.setDestinationLongitude(latestTrip.getDestination_Longitude());
        journeyModeDetails.setDestinationId(destinationStationId);
        Time sourceArival = Time.valueOf(latestTrip.getSource_Arival());
        Time destArival = Time.valueOf(latestTrip.getDestination_Arival());
        List<Time> departures = upcomingTrips.stream().map(s -> Time.valueOf(s.getSource_Arival())).collect(Collectors.toList());
        journeyModeDetails.setTimings(departures);
        journeyModeDetails.setTime(sourceArival);
        journeyModeDetails.setRoute(latestTrip.getRoute_Name());
        journeyModeDetails.setFare(latestTrip.getfare());
        journeyModeDetails.setEstimatedArrivalTime(destArival);
        journeyModeDetails.setTravelTime((double) getTimeDiff(destArival, sourceArival));
        try{
            redisClient.setValue(redisKey, CommonUtils.convertObjectToJsonString(journeyModeDetails));
        } catch (Exception ex) {
            log.info("Failed to cache bus journey data");
        }
        return journeyModeDetails;
    }

    private Double getBusStationDist(String sourceId, String destId,CoordinatesDto srcCoordinate,
                                     List<CoordinatesDto> destCoordinates){
        Double dist;
        String redKeyPrefix="bus_dist:";
        try{
            String redisKey = redKeyPrefix+sourceId+"-"+destId;
            return Double.valueOf(redisClient.getValue(redisKey));
        } catch (Exception ex){
            log.info("Failed to fetch Redis key: {}", ex.getMessage());
        }
        List<Map<String,Double>> distMap = osrmUtils.getOSRMDistance(srcCoordinate, destCoordinates);
        if(distMap.size()>0){
            dist = distMap.get(0).get("distance");
        } else {
            dist = DistanceHelper.getHaversine(srcCoordinate, destCoordinates.get(0));
            log.info("Harvisine dist=:{}", dist);
        }
        try{
            String redkey1 = redKeyPrefix+sourceId+"-"+destId;
            String redKey2 = redKeyPrefix+destId+"-"+sourceId;
            redisClient.setValue(redkey1, String.valueOf(dist));
            redisClient.setValue(redKey2, String.valueOf(dist));
        } catch (Exception ex){
            log.error("Failed to set redis key for bus station dist:{}",ex.getMessage());
        }
        return dist;
    }

}
