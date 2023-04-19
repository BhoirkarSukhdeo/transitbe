package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.journey.model.DTO.CoordinatesDto;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.journey.utils.OSRMUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTOInterface;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.service.impl.StationServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class StationServiceTests extends BaseTest {
    private final ModelMapper modelMapper = new ModelMapper();
    MetroStation metroStation;
    List<MetroStation> metroStations;

    @Mock
    MetroStationRepository metroStationRepository;
    @Mock
    JourneyService journeyService;
    @Mock
    OSRMUtils osrmUtils;

    @Autowired
    @InjectMocks
    StationServiceImpl stationService;

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
    }

    @Test
    public void getStationsTest() throws Exception {
        MetroStation metroStation = new MetroStation();
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        Assert.assertNotNull(stationService.getStations());
    }

    @Test
    public void getNearbyStationsTest() throws Exception {
        NearByStationsDTOInterface nearByStationsDTOInterface = new NearByStationsDTOInterface() {
            @Override
            public String getDisplay_Name() {
                return "PCC";
            }

            @Override
            public String getStation_Id() {
                return "123";
            }

            @Override
            public double getLatitude() {
                return 23.4;
            }

            @Override
            public double getLongitude() {
                return 45.5;
            }

            @Override
            public Double getDistance() {
                return 23.5;
            }

            @Override
            public Double getSqlDist() {
                return 12.3;
            }
        };

        List<NearByStationsDTOInterface> nearByStationsDTOInterfaces = new ArrayList<>();
        nearByStationsDTOInterfaces.add(nearByStationsDTOInterface);
        when(metroStationRepository.findAllStationsByLatLong(any(Double.class), any(Double.class), any(String.class))).thenReturn(nearByStationsDTOInterfaces);
        Map<String,Double> map = new HashMap<>();
        map.put("distance", 78.4);
        map.put("duration", 23.5);
        List<Map<String,Double>> mapList = new ArrayList<>();
        mapList.add(map);
        when(osrmUtils.getOSRMDistance(any(CoordinatesDto.class),anyList())).thenReturn(mapList);
        Assert.assertNotNull(stationService.getNearbyStations(23.4, 23.5, 34));
    }
}
