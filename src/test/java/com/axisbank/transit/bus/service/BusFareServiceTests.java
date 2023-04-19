package com.axisbank.transit.bus.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DAO.BusFare;
import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.repository.BusFareRepository;
import com.axisbank.transit.bus.repository.BusRouteRepository;
import com.axisbank.transit.bus.service.impl.BusFareServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BusFareServiceTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    BusStation busStation;
    List<BusStation> busStations;
    BusRoute busRoute;
    List<BusRoute> busRoutes;
    BusFare busFare;
    List<BusFare> busFares;

    @Mock
    private BusRouteRepository busRouteRepository;

    @Mock
    private BusFareRepository busFareRepository;

    @InjectMocks
    @Autowired
    BusFareServiceImpl busFareService;

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
        Set<BusFare> busFareSet = new HashSet<>();
        busFareSet.add(busFare);
        busRoute.setBusFareSet(busFareSet);
    }

    @Test
    public void updateFareStatusTest() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong("1"));
        when(busFareRepository.findDistinctRouteIds(any(Pageable.class))).thenReturn(ids);
        when(busRouteRepository.findByIdIn(ids)).thenReturn(busRoutes);
        Assert.assertNotNull(busFareService.getAllRouteFares(0, 10));
    }


}
