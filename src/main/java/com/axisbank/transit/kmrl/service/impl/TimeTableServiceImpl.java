package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.core.shared.utils.WorkFlowUtil;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTable;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTableType;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTableVer;
import com.axisbank.transit.kmrl.model.DTO.*;
import com.axisbank.transit.kmrl.repository.MetroTimeTableRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableTypeRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableVerRepository;
import com.axisbank.transit.kmrl.service.BookTicketService;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.utils.CommonUtils.*;
import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;

@Slf4j
@Service
public class TimeTableServiceImpl implements TimeTableService {
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    StationService stationService;

    @Autowired
    MetroTimeTableRepository metroTimeTableRepository;

    @Autowired
    MetroTimeTableTypeRepository metroTimeTableTypeRepository;

    @Autowired
    BookTicketService bookTicketService;

    @Autowired
    RedisClient redisClient;
    @Autowired
    MetroTimeTableVerRepository metroTimeTableVerRepository;

    Map<String, MetroStation> metroStationMap = new HashMap<>();

    @Override
    public List<MetroTimeTableTypesDTO> getAllMetroTimetables() {
        List<MetroTimeTableType> timeTableTypes = metroTimeTableTypeRepository.findAll();
        List<MetroTimeTableTypesDTO> timeTableTypesDTOS = new ArrayList<>();
        for(MetroTimeTableType metroTimeTableType: timeTableTypes){
            timeTableTypesDTOS.add(modelMapper.map(metroTimeTableType,MetroTimeTableTypesDTO.class));
        }
        return timeTableTypesDTOS;
    }

    @Override
    public List<MetroTimeTableAdminDTO> getMetroTimetableByType(String timetabletypeId, int page, int size, String searchParam) throws Exception {
        log.info("Request received inside getAllTimetable: ");
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        try {
            List<MetroTimeTableVer> metroTimeTables = metroTimeTableVerRepository.findAllByTrip_TimeTableType_MtTimetableIdAndStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(timetabletypeId, searchParam, searchParam, paging);
            List<MetroTimeTableAdminDTO> metroTimeTableAdminDTOS = new ArrayList<>();
            for (MetroTimeTableVer metroTimeTable: metroTimeTables) {
                MetroTimeTableAdminDTO metroTimeTableAdminDTO = modelMapper.map(metroTimeTable, MetroTimeTableAdminDTO.class);
                metroTimeTableAdminDTO.setDirection(metroTimeTable.getTrip().getDirection());
                metroTimeTableAdminDTO.setMetroTripNum(metroTimeTable.getTrip().getTripNumber());
                metroTimeTableAdminDTO.setTotalDistance(metroTimeTable.getTrip().getTotalDistance());
                metroTimeTableAdminDTOS.add(metroTimeTableAdminDTO);
            }
            return metroTimeTableAdminDTOS;
        } catch (Exception exception) {
            log.error("Error in getting Timetable: {}", exception.getMessage());
            throw new Exception("Error in getting Timetable, Please try again later.");
        }
    }

    @Override
    public void updateTimeTableStatus(String timetabletypeId, String status) throws Exception {
        MetroTimeTableType timeTableType = metroTimeTableTypeRepository.findByMtTimetableId(timetabletypeId);
        if (timeTableType==null)
            throw new Exception("Failed to fetch timetable got given TypeId");
        String currStatus= timeTableType.getCurrentStatus();
        if(WorkFlowUtil.verifyWorkFlow(currStatus, status)){
            timeTableType.setCurrentStatus(status);
            metroTimeTableTypeRepository.save(timeTableType);
            return;
        }
        log.info("Failed to change state of timetable as give status doesn't match workflow");
        throw new Exception("Invalid State change request");
    }

    @Override
    public List<MetroRouteDetailsDTO> getMetroDetails(String sourceStation, String destinationStation) throws ParseException {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        String maxTime = addSecondsToTime(currentTime, 15 * 60);
        MetroStation source = stationService.getStationById(sourceStation);
        MetroStation destination = stationService.getStationById(destinationStation);

        List<MetroTimeTable> upcomingArrivals = metroTimeTableRepository.
                findAllByStationAndArivalTimeGreaterThanEqualAndArivalTimeLessThanEqual(source,
                        Time.valueOf(currentTime), Time.valueOf(maxTime));
        List<MetroRouteDetailsDTO> metroRouteDetailsDTOS = new ArrayList<>();
        for (MetroTimeTable upcomingArrival : upcomingArrivals) {
            MetroTimeTable destinationTimetable = metroTimeTableRepository.findByStationAndTripAndSrNumGreaterThan(
                    destination, upcomingArrival.getTrip(), upcomingArrival.getSrNum()
            );
            if (destinationTimetable != null) {
                MetroRouteDetailsDTO metroRouteDetailsDTO = new MetroRouteDetailsDTO();
                MetroTimetableStationDTO sourceDTO = modelMapper.map(upcomingArrival, MetroTimetableStationDTO.class);
                sourceDTO.setDisplayName(source.getDisplayName());
                sourceDTO.setLatitude(source.getLatitude());
                sourceDTO.setLongitude(source.getLongitude());
                sourceDTO.setStationId(source.getStationId());
                metroRouteDetailsDTO.setSource(sourceDTO);

                MetroTimetableStationDTO destinationDTO = modelMapper.map(destinationTimetable, MetroTimetableStationDTO.class);
                destinationDTO.setDisplayName(destination.getDisplayName());
                destinationDTO.setLatitude(destination.getLatitude());
                destinationDTO.setLongitude(destination.getLongitude());
                destinationDTO.setStationId(destination.getStationId());
                metroRouteDetailsDTO.setDestination(destinationDTO);

                List<MetroTimeTable> intermediateStations = metroTimeTableRepository.findAllByTripAndSrNumGreaterThanAndSrNumLessThan(
                        upcomingArrival.getTrip(), upcomingArrival.getSrNum(), destinationTimetable.getSrNum()
                );
                List<MetroTimetableStationDTO> intermediateStationDetails = new ArrayList<>();
                for (MetroTimeTable timeTable : intermediateStations) {
                    MetroTimetableStationDTO intermediateDTO = modelMapper.map(timeTable, MetroTimetableStationDTO.class);
                    MetroStation interStation = timeTable.getStation();
                    intermediateDTO.setDisplayName(interStation.getDisplayName());
                    intermediateDTO.setLatitude(interStation.getLatitude());
                    intermediateDTO.setLongitude(interStation.getLongitude());
                    intermediateDTO.setStationId(interStation.getStationId());
                    intermediateStationDetails.add(intermediateDTO);
                }
                metroRouteDetailsDTO.setIntermediateStations(intermediateStationDetails);
                metroRouteDetailsDTOS.add(metroRouteDetailsDTO);
            }
        }
        return metroRouteDetailsDTOS;
    }

    @Override
    public JourneyModeDetails getMetroRouteDetails(String sourceStation, String destinationStation, String startTime) {
        if (sourceStation.equalsIgnoreCase(destinationStation)) return null;
        String redisKey = "metro_route:"+sourceStation+"-"+destinationStation;
        try{
            String journeyData = redisClient.getValue(redisKey);
            JourneyModeDetails journeyDetails= CommonUtils.convertJsonStringToObject(journeyData, JourneyModeDetails.class);
            List<Time> upcomingTimings = getUpcomingStationTimings(sourceStation, destinationStation, startTime);
            if (upcomingTimings.isEmpty())
                return null;
            journeyDetails.setTimings(upcomingTimings);
            journeyDetails.setTime(upcomingTimings.get(0));
            journeyDetails.setEstimatedArrivalTime(Time.valueOf(CommonUtils.addSecondsToTime(upcomingTimings.get(0).toString(), journeyDetails.getTravelTime().intValue())));
            log.info("Sending metro data from cache");
            return journeyDetails;
        } catch(Exception ex) {
            log.error("Failed to get metro journey cache data");
        }
        MetroStation source = stationService.getStationById(sourceStation);
        MetroStation destination = stationService.getStationById(destinationStation);
        List<MetroTimeTableDTOInterface> upcomingTrips = metroTimeTableRepository
                .findAllTripsBetweenStations(source.getId(), destination.getId(), "1970-01-01 " + startTime);

        if (upcomingTrips.size() < 1) return null;
        List<MetroTimeTable> timeTables = null;
        for (MetroTimeTableDTOInterface firstUpcoming : upcomingTrips) {
            timeTables = metroTimeTableRepository.findAllByTripIdAndSrNumBetweenOrderBySrNum(
                    firstUpcoming.getMetro_Trip_Id(), firstUpcoming.getSource_Sr_Num(), firstUpcoming.getDest_Sr_Num());
            if (timeTables != null) break;
        }
        if (timeTables == null || timeTables.size() < 2) return null;
        int destinationIndex = timeTables.size() - 1;

        MetroTimeTable destinationTimetable = timeTables.get(destinationIndex);
        MetroTimeTable sourceTimetable = timeTables.get(0);

        List<MetroTimeTable> intermediateStations = new ArrayList<>();
        try {
            intermediateStations = timeTables.subList(1, destinationIndex);
        } catch (IllegalArgumentException ex) {
            log.warn("No intermediate Stations Found: {}", ex.getMessage());
        }

        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(METRO);
        journeyModeDetails.setDistance((calculateMetroDistance(source.getDistance(), destination.getDistance())) * 1000);
        journeyModeDetails.setSource(source.getDisplayName());
        journeyModeDetails.setSourceLatitude(source.getLatitude());
        journeyModeDetails.setSourceLongitude(source.getLongitude());
        journeyModeDetails.setSourceId(source.getStationId());

        journeyModeDetails.setDestination(destination.getDisplayName());
        journeyModeDetails.setDestinationLatitude(destination.getLatitude());
        journeyModeDetails.setDestinationLongitude(destination.getLongitude());
        journeyModeDetails.setDestinationId(destination.getStationId());

        List<String> stationNames = intermediateStations.stream()
                .map(s -> s.getStation().getDisplayName())
                .collect(Collectors.toList());
        List<Time> metroTimings = upcomingTrips.stream()
                .map(s -> Time.valueOf(s.getArrival_Time()))
                .collect(Collectors.toList());
        journeyModeDetails.setIntermediateStops(stationNames);
        journeyModeDetails.setNoOfIntermediateStops(stationNames.size());
        journeyModeDetails.setTimings(metroTimings);
        journeyModeDetails.setTime(Time.valueOf(startTime));
        journeyModeDetails.setRoute(sourceTimetable.getMetroLineName());
        try{
            journeyModeDetails.setFare(Double.valueOf(bookTicketService.getTicketFare(source.getStationCode(), destination.getStationCode(), true)));
        } catch (Exception ex){
            log.error("Failed to fetch fare details: {}",ex.getMessage());
        }
        journeyModeDetails.setEstimatedArrivalTime(destinationTimetable.getArivalTime());
        journeyModeDetails.setTravelTime((double) getTimeDiff(destinationTimetable.getArivalTime(), sourceTimetable.getArivalTime()));

        try{
            redisClient.setValue(redisKey, CommonUtils.convertObjectToJsonString(journeyModeDetails));
        } catch (Exception ex) {
            log.info("Failed to cache journey data");
        }

        return journeyModeDetails;
    }

    @Override
    public JourneyModeDetails getMetroRouteDetails(String sourceStation, String destinationStation) {
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        return getMetroRouteDetails(sourceStation, destinationStation, currentTime);
    }

    @Override
    public MetroStation getMetroStation(String stationCode) {
        stationCode = stationCode.trim();
        MetroStation metroStation = metroStationMap.get(stationCode);
        if (metroStation != null) return metroStation;
        metroStation = stationService.getStationByStationCode(stationCode);
        metroStationMap.put(stationCode, metroStation);
        return metroStation;
    }

    @Override
    public List<Time> getUpcomingTimings(String sourceStation, String destinationStation) {
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        return getUpcomingTimings(sourceStation, destinationStation, currentTime);
    }

    @Override
    public List<Time> getUpcomingDepartures(String sourceStation) {
        MetroStation source = stationService.getStationById(sourceStation);
        String currentTime = CommonUtils.getCurrentDateTime("HH:mm:ss");
        List<MetroTimeTable> metroTimeTables =  metroTimeTableRepository.findAllByStationAndDepartureTimeGreaterThanOrderByDepartureTimeAsc(source,
                Time.valueOf(currentTime));
        return metroTimeTables.stream()
                .map(MetroTimeTable::getDepartureTime)
                .collect(Collectors.toList());
    }

    @Override
    public List<Time> getUpcomingTimings(String sourceStation, String destinationStation, String startTime) {
        MetroStation source = stationService.getStationById(sourceStation);
        MetroStation destination = stationService.getStationById(destinationStation);
        List<MetroTimeTableDTOInterface> upcomingTrips = metroTimeTableRepository
                .findAllTripsBetweenStations(source.getId(), destination.getId(), "1970-01-01 " + startTime);
        return upcomingTrips.stream()
                .map(s -> Time.valueOf(s.getArrival_Time()))
                .collect(Collectors.toList());
    }

    public List<Time> getUpcomingStationTimings(String sourceStation, String destinationStation, String startTime) {
        List<Time> allData = new ArrayList<>();
        String redisKey = "metro_upcoming:"+sourceStation+"-"+destinationStation;
        try{
            String times = redisClient.getValue(redisKey);
            allData = objectMapper.readValue(times, new TypeReference<List<Time>>(){});
        } catch (Exception ex){
            log.error("Failed to get timings from redis:{}", ex.getMessage());
        }
        if(allData.isEmpty()){
            MetroStation source = stationService.getStationById(sourceStation);
            MetroStation destination = stationService.getStationById(destinationStation);
            List<MetroTimeTableDTOInterface> upcomingTrips = metroTimeTableRepository
                    .findAllTripsBetweenStations(source.getId(), destination.getId(), "1970-01-01 00:00:00");
            allData = upcomingTrips.stream()
                    .map(s -> Time.valueOf(s.getArrival_Time()))
                    .collect(Collectors.toList());
            try{
                redisClient.setValue(redisKey, CommonUtils.convertObjectToJsonString(allData), 6*60*60);
            } catch (Exception ex){
                log.error("Failed to store upcoming data");
            }
        }
        allData.removeIf(p -> p.getTime() < Time.valueOf(startTime).getTime());
        return allData;
    }
}
