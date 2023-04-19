package com.axisbank.transit.bus.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DAO.*;
import com.axisbank.transit.bus.model.DTO.BusFareUpdateDTO;
import com.axisbank.transit.bus.model.DTO.BusStationDTO;
import com.axisbank.transit.bus.model.DTO.BusTimeTableUpdateDTO;
import com.axisbank.transit.bus.repository.*;
import com.axisbank.transit.bus.service.impl.BusAdminServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BusAdminServiceTests extends BaseTest {
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

    @InjectMocks
    @Autowired
    BusAdminServiceImpl busAdminService;

    @Mock
    BusRouteRepository busRouteRepository;

    @Mock
    BusStationRepository busStationRepository;

    @Mock
    BusFareRepository busFareRepository;

    @Mock
    BusTimeTableRepository busTimeTableRepository;
    @Mock
    BusTimetableTypeRepository busTimetableTypeRepository;
    @Mock
    BusFareTypeRepository busFareTypeRepository;
    @Mock
    BusTimeTableVerRepository busTimeTableVerRepository;
    @Mock
    BusFareVerRepository busFareVerRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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
    public void getAllStationsTest() throws Exception {
        when(busStationRepository.findAllByDisplayNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(busStations);
        Assert.assertNotNull(busAdminService.getAllStations(0, 10, "PCC"));
    }

    @Test
    public void getAllStationsExceptionTest() throws Exception {
        when(busStationRepository.findAllByDisplayNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(null);
        try {
            busAdminService.getAllStations(0, 10, "PCC");
        } catch (Exception exception) {
            Assert.assertEquals("Error in getting Stations, Please try again later.", exception.getMessage());
        }
    }

    @Test
    public void getAllRoutesTest() throws Exception {
        when(busRouteRepository.findAllByRouteCodeContainingIgnoreCaseOrVehicleNumberContainingIgnoreCaseOrBusTypeContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(busRoutes);
        Assert.assertNotNull(busAdminService.getAllRoutes(0, 10, "%"));
    }

    @Test
    public void getAllFaresTest() throws Exception {
        when(busFareRepository.findAllByBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(busFares);
        Assert.assertNotNull(busAdminService.getAllFares(0, 10, "%"));
    }

    @Test
    public void getAllTimetablesTest() throws Exception {
        when(busTimeTableRepository.findAllByBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(any(String.class), any(String.class), any(Pageable.class))).thenReturn(busTimeTables);
        Assert.assertNotNull(busAdminService.getAllTimetable(0, 10, "%"));
    }

    @Test
    public void updateStationTest() throws Exception {
        BusStationDTO busStationDTO = new BusStationDTO();
        busStationDTO.setStationId("123");
        busStationDTO.setDisplayName("Aluva");
        busStationDTO.setLatitude(23.6);
        busStationDTO.setLongitude(45.6);
        when(busStationRepository.findByStationId(any(String.class))).thenReturn(busStation);
        busAdminService.updateStation(busStationDTO);
        Assert.assertEquals("Aluva", busStation.getDisplayName());
    }

    @Test
    public void updateFareTest() throws Exception {
        BusFareUpdateDTO busFareUpdateDTO = new BusFareUpdateDTO();
        busFareUpdateDTO.setFare(20);
        busFareUpdateDTO.setFareId("123");
        when(busFareRepository.findByFareId(any(String.class))).thenReturn(busFare);
        busAdminService.updateFare(busFareUpdateDTO);
        Assert.assertEquals("20.0", String.valueOf(busFare.getFare()));
    }

    @Test
    public void updateTimeTableTest() throws Exception {
        BusTimeTableUpdateDTO busTimeTableUpdateDTO = new BusTimeTableUpdateDTO();
        busTimeTableUpdateDTO.setTimeTableId("123");
        Time time = java.sql.Time.valueOf("07:20:00");
        busTimeTableUpdateDTO.setArivalTime(time);
        busTimeTableUpdateDTO.setDepartureTime(time);
        when(busTimeTableRepository.findByTimeTableId(any(String.class))).thenReturn(busTimeTable);
        busAdminService.updateTimetable(busTimeTableUpdateDTO);
        Assert.assertEquals("07:20:00", busTimeTable.getArivalTime().toString());
    }

    @Test
    public void getAllBusTimetablesTest() throws Exception {
        when(busTimetableTypeRepository.findAll()).thenReturn(timeTableTypes);
        Assert.assertNotNull(busAdminService.getAllBusTimetables());
    }

    @Test
    public void getBusTimetableByTypeTest() throws Exception {
        when(busTimeTableVerRepository.findAllByTimeTableType_BusTimetableIdAndBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(busTimeTableVers);
        Assert.assertNotNull(busAdminService.getBusTimetableByType("123", 0, 10, "%"));
    }

    @Test
    public void updateBusTimeTableStatusTest() throws Exception {
        when(busTimetableTypeRepository.findByBusTimetableId(any(String.class))).thenReturn(busTimeTableType);
        when(busTimetableTypeRepository.save(any(BusTimeTableType.class))).thenReturn(busTimeTableType);
        busAdminService.updateBusTimeTableStatus("123", "approved");
        Assert.assertEquals("approved", busTimeTableType.getCurrentStatus());
    }

    @Test
    public void getAllBusFaresTest() throws Exception {
        when(busFareTypeRepository.findAll()).thenReturn(busFareTypes);
        Assert.assertNotNull(busAdminService.getAllBusFares());
    }

    @Test
    public void getBusFareByTypeTest() throws Exception {
        when(busFareVerRepository.findAllByBusFareType_BusFareTypeIdAndBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(busFareVers);
        Assert.assertNotNull(busAdminService.getBusFareByType("123", 0, 10, "%"));
    }

    @Test
    public void updateFareStatusTest() throws Exception {
        when(busFareTypeRepository.findByBusFareTypeId(any(String.class))).thenReturn(busFareType);
        when(busFareTypeRepository.save(any(BusFareType.class))).thenReturn(busFareType);
        busAdminService.updateFareStatus("123", "approved");
    }
}
