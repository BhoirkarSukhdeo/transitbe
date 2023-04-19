package com.axisbank.transit.bus.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DAO.*;
import com.axisbank.transit.bus.model.DTO.BusRouteRedisDTO;
import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.bus.model.DTO.BusTimeTableFareInterface;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.repository.BusTimeTableRepository;
import com.axisbank.transit.bus.service.impl.BusTimetableImpl;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class BusTimeTableServiceTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    BusStation busStation;
    List<BusStation> busStations;
    BusRoute busRoute;
    List<BusRoute> busRoutes;
    BusFare busFare;
    List<BusFare> busFares;
    BusTimeTable busTimeTable;
    List<BusTimeTable> busTimeTables;
    Time time;
    BusTimeTableType busTimeTableType;
    List<BusTimeTableType> timeTableTypes;
    BusTimeTableVer busTimeTableVer;
    List<BusTimeTableVer> busTimeTableVers;
    BusFareType busFareType;
    List<BusFareType> busFareTypes;
    BusFareVer busFareVer;
    List<BusFareVer> busFareVers;

    @Mock
    BusTimeTableRepository busTimeTableRepository;
    @Mock
    BusStationRepository busStationRepository;
    @Mock
    RedisClient redisClient;
    @Mock
    JourneyService journeyService;
    @Mock
    OSRMUtils osrmUtils;

    @InjectMocks
    @Autowired
    BusTimetableImpl busTimeTableService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);

        busStation = new BusStation();
        busStation.setDisplayName("PCC");
        busStation.setLatitude(23.4);
        busStation.setLongitude(34.5);
        busStation.setStationCode("PCC");
        busStation.setStationId("bus123");

        busStations = new ArrayList<>();
        busStations.add(busStation);

        busRoute = new BusRoute();
        busRoute.setRouteCode("ELOOR MACHANAM - W ISLAND VIA MENAKA-KL-01-AQ-7722");
        busRoute.setRouteName("ELOOR MACHANAM - W ISLAND VIA MENAKA");
        busRoute.setRouteNameUp("ELOOR MACHANAM - W ISLAND VIA MENAKA");
        busRoute.setRouteNameUp("W ISLAND - ELOOR MACHANAM VIA MENAKA");
        busRoute.setRouteId("route123");
        busRoute.setBusType("normal");
        busRoute.setAssociation("KOCHIWHEELZ");
        busRoute.setVehicleNumber("KL-01-AQ-7722");
        busRoute.getBusStationSet().add(busStation);

        busRoutes = new ArrayList<>();
        busRoutes.add(busRoute);

        busFare = new BusFare();
        busFare.setFareId("fare123");
        busFare.setFare(18.0);
        busFare.setToBusStation(busStation);
        busFare.setFromBusStation(busStation);
        busFare.setBusRoute(busRoute);

        busFares = new ArrayList<>();
        busFares.add(busFare);

        time = java.sql.Time.valueOf("06:20:00");
        busTimeTable = new BusTimeTable();
        busTimeTable.setTimeTableId("timetable123");
        busTimeTable.setArivalTime(time);
        busTimeTable.setDepartureTime(time);
        busTimeTable.setBusRoute(busRoute);
        busTimeTable.setBusStation(busStation);
        busTimeTable.setRouteType("Up");
        busTimeTable.setSrNum(1);
        busTimeTable.setTripNumber(1);

        busTimeTables = new ArrayList<>();
        busTimeTables.add(busTimeTable);

        busTimeTableVer = new BusTimeTableVer();
        busTimeTableVer.setTimeTableId("timetable123");
        busTimeTableVer.setArivalTime(time);
        busTimeTableVer.setDepartureTime(time);
        busTimeTableVer.setBusRoute(busRoute);
        busTimeTableVer.setBusStation(busStation);
        busTimeTableVer.setRouteType("Up");
        busTimeTableVer.setSrNum(1);
        busTimeTableVer.setTripNumber(1);

        busTimeTableVers = new ArrayList<>();
        busTimeTableVers.add(busTimeTableVer);

        busTimeTableType = new BusTimeTableType();
        busTimeTableType.setBusTimetableId("busTimetable123");
        busTimeTableType.setTimeTableName("timetable1");
        busTimeTableType.setCurrentStatus("created");
        busTimeTableType.setActiveDays("Sunday");
        busTimeTableType.setTimeTables(busTimeTableVers);

        timeTableTypes = new ArrayList<>();
        timeTableTypes.add(busTimeTableType);

        busFareVer = new BusFareVer();
        busFareVer.setFareId("fare123");
        busFareVer.setFare(18.0);
        busFareVer.setToBusStation(busStation);
        busFareVer.setFromBusStation(busStation);
        busFareVer.setBusRoute(busRoute);

        busFareVers = new ArrayList<>();
        busFareVers.add(busFareVer);

        busFareType = new BusFareType();
        busFareType.setCurrentStatus("created");
        busFareType.setBusFareTypeId("busfare123");
        busFareType.setBusFareName("busFare1");
        busFareType.setActiveDays("Friday");
        busFareType.setFares(busFareVers);

        busFareTypes = new ArrayList<>();
        busFareTypes.add(busFareType);
    }

    @Test
    public void getUpcomingDeparturesTest() throws Exception {
        when(CommonUtils.getCurrentDateTime(any(String.class))).thenReturn("09:30:00");
        when(busTimeTableRepository.findAllByBusStation_StationIdAndArivalTimeGreaterThanOrderByArivalTimeAsc(any(String.class), any(Time.class))).thenReturn(busTimeTables);
        Assert.assertNotNull(busTimeTableService.getUpcomingDepartures("Aluva"));
    }

    @Test
    public void getUpcomingTimingsTest() throws Exception {

        BusTimeTableFareInterface busTimeTableFareInterface = new BusTimeTableFareInterface() {
            @Override
            public long getTrip_Number() {
                return 1;
            }

            @Override
            public String getTrip_Type() {
                return "up";
            }

            @Override
            public String getSource_Display_Name() {
                return "abc";
            }

            @Override
            public double getSource_Latitude() {
                return 79.45;
            }

            @Override
            public double getSource_Longitude() {
                return 78.34;
            }

            @Override
            public String getSource_Arival() {
                return "07:30:00";
            }

            @Override
            public String getDestination_Display_Name() {
                return "abc";
            }

            @Override
            public double getDestination_Latitude() {
                return 76.89;
            }

            @Override
            public double getDestination_Longitude() {
                return 3.3;
            }

            @Override
            public String getDestination_Arival() {
                return "07:30:00";
            }

            @Override
            public double getfare() {
                return 5.0;
            }

            @Override
            public String getRoute_Name() {
                return "routeA";
            }

            @Override
            public String getBus_Type() {
                return "normal";
            }
        };
        List<BusTimeTableFareInterface> upcomingTrips = new ArrayList<>();
        upcomingTrips.add(busTimeTableFareInterface);
        when(busTimeTableRepository.getMetroRouteStationsTimetableAndFare(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(upcomingTrips);

        Assert.assertNotNull(busTimeTableService.getUpcomingTimings("Aluva", "PCC"));
    }

    @Test
    public void getRouteDijktrasTest() throws Exception {

        String sourceStationId = "1223";
        String destinationStationId = "3221";
        String redisKey = "bus_route:"+sourceStationId+"-"+destinationStationId;

        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setCalories(200.3);
        journeyModeDetails.setDestination("PCC");
        journeyModeDetails.setFare(20.3);
        journeyModeDetails.setTime(time);
        journeyModeDetails.setDestinationId("234");
        journeyModeDetails.setDestinationLatitude(23.5);
        journeyModeDetails.setDestinationLongitude(23.5);
        journeyModeDetails.setDistance(100.2);
        journeyModeDetails.setEstimatedArrivalTime(time);
        journeyModeDetails.setIntermediateStops(Arrays.asList("ABC"));
        journeyModeDetails.setRoute("xyz");
        journeyModeDetails.setSource("ALUVA");
        journeyModeDetails.setSourceLatitude(23.4);
        journeyModeDetails.setSourceLongitude(25.4);
        journeyModeDetails.setType("walk");
        journeyModeDetails.setTimings(Arrays.asList(time));
        journeyModeDetails.setTravelTime(300.0);

        doNothing().when(redisClient).setValue(any(String.class), any(String.class));
        when(redisClient.getValue(redisKey)).thenReturn("abc");
        PowerMockito.when(CommonUtils.convertJsonStringToObject("abc", JourneyModeDetails.class)).thenReturn(journeyModeDetails);


        PowerMockito.when(CommonUtils.getCurrentDateTime("HH:mm:ss")).thenReturn("07:30:00");

        BusTimeTableFareInterface busTimeTableFareInterface = new BusTimeTableFareInterface() {
            @Override
            public long getTrip_Number() {
                return 1;
            }

            @Override
            public String getTrip_Type() {
                return "up";
            }

            @Override
            public String getSource_Display_Name() {
                return "abc";
            }

            @Override
            public double getSource_Latitude() {
                return 79.45;
            }

            @Override
            public double getSource_Longitude() {
                return 78.34;
            }

            @Override
            public String getSource_Arival() {
                return "07:30:00";
            }

            @Override
            public String getDestination_Display_Name() {
                return "abc";
            }

            @Override
            public double getDestination_Latitude() {
                return 76.89;
            }

            @Override
            public double getDestination_Longitude() {
                return 3.3;
            }

            @Override
            public String getDestination_Arival() {
                return "07:30:00";
            }

            @Override
            public double getfare() {
                return 5.0;
            }

            @Override
            public String getRoute_Name() {
                return "routeA";
            }

            @Override
            public String getBus_Type() {
                return "normal";
            }
        };
        List<BusTimeTableFareInterface> upcomingTrips = new ArrayList<>();
        upcomingTrips.add(busTimeTableFareInterface);
        when(busTimeTableRepository.getMetroRouteStationsTimetableAndFare(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(upcomingTrips);


        BusSrcDestRouteDTO busSrcDestRouteDTO = new BusSrcDestRouteDTO();
        busSrcDestRouteDTO.setDestinationStation("PCC");
        busSrcDestRouteDTO.setRouteCode("ELOOR MACHANAM - W ISLAND VIA MENAKA-KL-01-AQ-7722");
        busSrcDestRouteDTO.setSrNum(1);
        busSrcDestRouteDTO.setSourceStation("ALUVA");
        List<BusSrcDestRouteDTO> busSrcDestRouteDTOList = new ArrayList<>();
        busSrcDestRouteDTOList.add(busSrcDestRouteDTO);

        BusRouteRedisDTO busRouteRedisDTO = new BusRouteRedisDTO();
        busRouteRedisDTO.setDestination("PCC");
        busRouteRedisDTO.setSource("ALUVA");
        busRouteRedisDTO.setBusSrcDestRouteDTOList(busSrcDestRouteDTOList);

        PowerMockito.when(CommonUtils.convertJsonStringToObject("abc", BusRouteRedisDTO.class)).thenReturn(busRouteRedisDTO);
        Assert.assertNotNull(busTimeTableService.getRouteDijktras(sourceStationId, destinationStationId));

    }
}
