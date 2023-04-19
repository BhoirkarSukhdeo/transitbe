package com.axisbank.transit.bus.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTOInterface;
import com.axisbank.transit.bus.repository.BusRouteRepository;
import com.axisbank.transit.bus.repository.BusStationRepository;
import com.axisbank.transit.bus.service.impl.BusStationServiceImpl;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BusStationServiceTests extends BaseTest {
    BusStation busStation1;
    BusStation busStation2;
    List<BusStation> busStations;
    BusRoute busRoute;
    List<BusRoute> busRoutes;

    @Mock
    BusStationRepository busStationRepository;
    @Mock
    BusRouteRepository busRouteRepository;
    @Mock
    OSRMUtils osrmUtils;
    @Mock
    JourneyService journeyService;

    @InjectMocks
    @Autowired
    BusStationServiceImpl busStationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        busStation1 = new BusStation();
        busStation1.setDisplayName("PCC");
        busStation1.setLatitude(23.4);
        busStation1.setLongitude(34.5);
        busStation1.setStationCode("PCC");
        busStation1.setStationId("bus123");

        busStation2 = new BusStation();
        busStation2.setDisplayName("ELOOR MACHANAM");
        busStation2.setLatitude(25.4);
        busStation2.setLongitude(45.5);
        busStation2.setStationCode("ELOOR MACHANAM");
        busStation2.setStationId("bus124");

        busStations = new ArrayList<>();
        busStations.add(busStation1);
        busStations.add(busStation2);

        busRoute = new BusRoute();
        busRoute.setRouteCode("ELOOR MACHANAM - W ISLAND VIA MENAKA-KL-01-AQ-7722");
        busRoute.setRouteName("ELOOR MACHANAM - W ISLAND VIA MENAKA");
        busRoute.setRouteNameUp("ELOOR MACHANAM - W ISLAND VIA MENAKA");
        busRoute.setRouteNameDown("W ISLAND - ELOOR MACHANAM VIA MENAKA");
        busRoute.setRouteId("route123");
        busRoute.setBusType("normal");
        busRoute.setAssociation("KOCHIWHEELZ");
        busRoute.setVehicleNumber("KL-01-AQ-7722");
        busRoute.getBusStationSet().add(busStation1);

        busRoutes = new ArrayList<>();
        busRoutes.add(busRoute);
    }

    @Test
    public void getNearByStationsTest() throws Exception {
        NearByBusStationsDTOInterface nearByBusStationsDTOInterface = new NearByBusStationsDTOInterface() {
            @Override
            public String getDisplay_Name() {
                return "PCC";
            }

            @Override
            public String getStation_Id() {
                return "1";
            }

            @Override
            public double getLatitude() {
                return 34.5;
            }

            @Override
            public double getLongitude() {
                return 45.6;
            }

            @Override
            public Double getSqlDist() {
                return 67.9;
            }
        };
        List<NearByBusStationsDTOInterface> nearByBusStationsDTOInterfaceList = new ArrayList<>();
        nearByBusStationsDTOInterfaceList.add(nearByBusStationsDTOInterface);
        Map<String,Double> distanceMap = new HashMap<>();
        distanceMap.put("distance", 34.5);
        distanceMap.put("duration", 30.0);
        List<Map<String,Double>> distances = new ArrayList<>();
        distances.add(distanceMap);

        when(busStationRepository.findAllStationsByLatLong(any(Double.class), any(Double.class), any(String.class))).thenReturn(nearByBusStationsDTOInterfaceList);
        when(osrmUtils.getOSRMDistance(any(CoordinatesDto.class),anyList())).thenReturn(distances);
        when(journeyService.getNearByStationRadiusGC()).thenReturn(67.9);
        Assert.assertNotNull(busStationService.getNearbyStations(23.3, 34.5));
    }

    @Test
    public void getStationsTest() throws Exception {
        when(busRouteRepository.findAllByBusType(any(String.class))).thenReturn(busRoutes);
        Assert.assertNotNull(busStationService.getStations("bus123", "normal"));
    }

    @Test
    public void getNearByBusStationSQLTest() throws Exception {
        NearByBusStationsDTOInterface nearByBusStationsDTOInterface = new NearByBusStationsDTOInterface() {
            @Override
            public String getDisplay_Name() {
                return "PCC";
            }

            @Override
            public String getStation_Id() {
                return "1";
            }

            @Override
            public double getLatitude() {
                return 34.5;
            }

            @Override
            public double getLongitude() {
                return 45.6;
            }

            @Override
            public Double getSqlDist() {
                return 67.9;
            }
        };
        List<NearByBusStationsDTOInterface> nearByBusStationsDTOInterfaceList = new ArrayList<>();
        nearByBusStationsDTOInterfaceList.add(nearByBusStationsDTOInterface);

        when(busStationRepository.findAllStationsByLatLong(any(Double.class), any(Double.class), any(String.class))).thenReturn(nearByBusStationsDTOInterfaceList);
        Assert.assertNotNull(busStationService.getNearByBusStationSQL(23.3, 34.5, 23.7));
    }
}
