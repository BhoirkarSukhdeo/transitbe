package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.kmrl.model.DTO.MetroLineDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.service.impl.MetroLineServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;

public class MetroLineServiceImplTest extends BaseTest {

    @Mock
    StationService stationService;

    @Autowired
    @InjectMocks
    MetroLineServiceImpl metroLineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveLineTest() throws Exception {
        MetroLineDTO metroLineDTO = new MetroLineDTO();
        MetroStationDTO metroStationDTO = new MetroStationDTO();
        metroStationDTO.setStationId("123");
        metroStationDTO.setDisplayName("Aluva");
        metroStationDTO.setLatitude(223.4);
        metroStationDTO.setLongitude(234.5);
        Set<MetroStationDTO> metroStationDTOSet = new HashSet<>();
        metroStationDTOSet.add(metroStationDTO);

        metroLineDTO.setStation(metroStationDTOSet);
        metroLineDTO.setDisplayName("Blue");
        doNothing().when(stationService).saveStation(anyList());
        metroLineService.saveLine(metroLineDTO);
    }

}
