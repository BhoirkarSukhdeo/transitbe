package com.axisbank.transit.journey.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTO;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.service.BusStationService;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.services.impl.JourneyServiceImpl;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroLine;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTable;
import com.axisbank.transit.kmrl.model.DAO.MetroTrip;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTO;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.service.StationService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.util.*;

import static com.axisbank.transit.journey.constants.JourneyTypes.METRO;
import static com.axisbank.transit.journey.constants.JourneyTypes.WALK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JourneyServiceTests extends BaseTest {
    MetroStation metroStation;
    List<MetroStation> metroStations;
    MetroLine metroLine;
    List<MetroLine> metroLines;
    MetroTimeTable metroTimeTable;
    List<MetroTimeTable> metroTimeTables;
    MetroTrip metroTrip;

    @InjectMocks
    @Autowired
    JourneyServiceImpl journeyService;

    @Mock
    StationService stationService;

    @Mock
    TimeTableService timeTableService;

    @Mock
    BusTimeTableService busTimeTableService;

    @Mock
    BusStationService busStationService;

    @Mock
    BusStationRepository busStationRepository;
    @Mock
    RedisClient redisClient;
    @Mock
    MetroStationRepository metroStationRepository;
    @Mock
    OSRMUtils osrmUtils;
    @Mock
    GlobalConfigService globalConfigService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        metroStation = new MetroStation();
        metroStation.setDisplayName("Aluva");
        metroStation.setSetStationId(1);
        metroStation.setStationCodeUp("STA_COD_3105T_UP");
        metroStation.setStationCodeDn("STA_COD_3106T_UP");
        metroStation.setStationCode("ALVA");
        metroStation.setStationId("metro123");
        metroStation.setDistance(34.5);
        metroStation.setLatitude(76.6);
        metroStation.setLongitude(78.6);

        metroStations = new ArrayList<>();
        metroStations.add(metroStation);

        metroLine = new MetroLine();
        metroLine.setLineCode("blue");
        metroLine.setLineId("line123");
        metroLine.setDisplayName("Blue");

        metroLines = new ArrayList<>();
        metroLines.add(metroLine);

        metroTrip = new MetroTrip();
        metroTrip.setMtTripId("123");
        metroTrip.setTripId("123");
        metroTrip.setServiceId("234");
        metroTrip.setDirection("up");
        metroTrip.setNextNumber("2");
        metroTrip.setPrevNumber("1");
        metroTrip.setStartTime(Time.valueOf("02:30:00"));
        metroTrip.setTotalDistance("100");
        metroTrip.setTripNumber("1");

        metroTimeTable = new MetroTimeTable();
        metroTimeTable.setArivalTime(Time.valueOf("02:30:00"));
        metroTimeTable.setDepartureTime(Time.valueOf("03:30:00"));
        metroTimeTable.setTimeTableId("timetable123");
        metroTimeTable.setDwelltime(300);
        metroTimeTable.setMetroLineName("blue");
        metroTimeTable.setPrevRuntime(200);
        metroTimeTable.setSrNum(2);
        metroTimeTable.setStation(metroStation);
        metroTimeTable.setTotalRuntime(500);
        metroTimeTable.setTrip(metroTrip);

        metroTimeTables = new ArrayList<>();
        metroTimeTables.add(metroTimeTable);
    }

    @Test
    public void getRoutesTest() throws Exception {
        NearByStationsDTO nearByStationsDTO = new NearByStationsDTO();
        nearByStationsDTO.setDisplayName("PCC");
        nearByStationsDTO.setDistance(23.5);
        nearByStationsDTO.setStationId("123");
        nearByStationsDTO.setLatitude(76.3445);
        nearByStationsDTO.setLongitude(78.5643);
        nearByStationsDTO.setDuration(300.0);
        nearByStationsDTO.setOsrmDistance(34.7);
        nearByStationsDTO.setOsrmDuration(78.9);
        nearByStationsDTO.setSqlDist(23.5);
        nearByStationsDTO.toString();
        List<NearByStationsDTO> nearBySourceStationsDTOList = new ArrayList<>();
        nearBySourceStationsDTOList.add(nearByStationsDTO);
        when(stationService.getNearbyStations(any(Double.class), any(Double.class))).thenReturn(nearBySourceStationsDTOList);

        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(WALK);
        journeyModeDetails.setDistance(23.4);
        journeyModeDetails.setSourceLatitude(34.5);
        journeyModeDetails.setSourceLongitude(12.3);
        journeyModeDetails.setDestinationLatitude(56.5);
        journeyModeDetails.setDestinationLongitude(34.5);
        journeyModeDetails.setTime(Time.valueOf("07:30:00"));
        journeyModeDetails.setEstimatedArrivalTime(Time.valueOf("02:30:00"));
        journeyModeDetails.setTravelTime(23.5);
        journeyModeDetails.setSourceId("123");
        journeyModeDetails.setDestinationId("123");
        when(timeTableService.getMetroRouteDetails(any(String.class), any(String.class), any(String.class))).thenReturn(null);

        NearByBusStationsDTO nearByBusStationsDTO = new NearByBusStationsDTO();
        nearByBusStationsDTO.setStationId("bus123");
        nearByBusStationsDTO.setDisplayName("PCC");
        nearByBusStationsDTO.setLatitude(76.456);
        nearByBusStationsDTO.setLongitude(78.345);
        nearByBusStationsDTO.setDuration(34.7);
        nearByBusStationsDTO.setOsrmDistance(45.6);
        nearByBusStationsDTO.setOsrmDuration(45.3);
        nearByBusStationsDTO.setSqlDist(67.8);

        NearByBusStationsDTO nearByBusStationsDTO1 = new NearByBusStationsDTO();
        nearByBusStationsDTO1.setStationId("bus321");
        nearByBusStationsDTO1.setDisplayName("PCC");
        nearByBusStationsDTO1.setLatitude(76.456);
        nearByBusStationsDTO1.setLongitude(78.345);
        nearByBusStationsDTO1.setDuration(34.7);
        nearByBusStationsDTO1.setOsrmDistance(45.6);
        nearByBusStationsDTO1.setOsrmDuration(45.3);
        nearByBusStationsDTO1.setSqlDist(67.8);

        List<NearByBusStationsDTO> nearByBusStationsDTOS = new ArrayList<>();
        nearByBusStationsDTOS.add(nearByBusStationsDTO);
        List<NearByBusStationsDTO> nearByBusStationsDTOS1 = new ArrayList<>();
        nearByBusStationsDTOS1.add(nearByBusStationsDTO1);
        when(busStationService.getNearbyStations(76.234, 78.345)).thenReturn(nearByBusStationsDTOS);
        when(busStationService.getNearbyStations(76.334, 78.445)).thenReturn(nearByBusStationsDTOS1);

        SortedSet<JourneyModeDetails> getRouteJourney =
                new TreeSet<>(Comparator.comparing(JourneyModeDetails::getTime));
        getRouteJourney.add(journeyModeDetails);
        when(busTimeTableService.getRouteDijktras(any(String.class), any(String.class), any(String.class))).thenReturn(getRouteJourney);

        String source = "76.234,78.345";
        String destination = "76.334,78.445";
        Assert.assertNotNull(journeyService.getRoutes(source, destination, "all"));
    }

    @Test
    public void getRoutesMetroTest() throws Exception {
        NearByStationsDTO nearByStationsDTO = new NearByStationsDTO();
        nearByStationsDTO.setDisplayName("PCC");
        nearByStationsDTO.setDistance(23.5);
        nearByStationsDTO.setStationId("123");
        nearByStationsDTO.setLatitude(76.3445);
        nearByStationsDTO.setLongitude(78.5643);
        nearByStationsDTO.setDuration(300.0);
        nearByStationsDTO.setOsrmDistance(34.7);
        nearByStationsDTO.setOsrmDuration(78.9);
        nearByStationsDTO.setSqlDist(23.5);
        nearByStationsDTO.toString();
        List<NearByStationsDTO> nearBySourceStationsDTOList = new ArrayList<>();
        nearBySourceStationsDTOList.add(nearByStationsDTO);
        when(stationService.getNearbyStations(any(Double.class), any(Double.class))).thenReturn(nearBySourceStationsDTOList);

        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(WALK);
        journeyModeDetails.setDistance(23.4);
        journeyModeDetails.setSourceLatitude(34.5);
        journeyModeDetails.setSourceLongitude(12.3);
        journeyModeDetails.setDestinationLatitude(56.5);
        journeyModeDetails.setDestinationLongitude(34.5);
        journeyModeDetails.setTime(Time.valueOf("07:30:00"));
        journeyModeDetails.setEstimatedArrivalTime(Time.valueOf("02:30:00"));
        journeyModeDetails.setTravelTime(23.5);
        journeyModeDetails.setSourceId("123");
        journeyModeDetails.setDestinationId("123");
        when(timeTableService.getMetroRouteDetails(any(String.class), any(String.class), any(String.class))).thenReturn(journeyModeDetails);

        String source = "76.234,78.345";
        String destination = "76.234,78.345";
        Assert.assertNotNull(journeyService.getRoutes(source, destination, METRO));
    }
}
