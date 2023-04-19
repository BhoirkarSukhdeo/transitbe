package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.kmrl.model.DAO.MetroLine;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DAO.MetroTimeTable;
import com.axisbank.transit.kmrl.model.DAO.MetroTrip;
import com.axisbank.transit.kmrl.model.DTO.MetroLineAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationMappedDTO;
import com.axisbank.transit.kmrl.repository.MetroLineRepository;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableRepository;
import com.axisbank.transit.kmrl.service.impl.KmrlAdminServiceImpl;
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
import org.springframework.data.domain.Pageable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class KmrlAdminServiceTest extends BaseTest {
    MetroStation metroStation;
    List<MetroStation> metroStations;
    MetroLine metroLine;
    List<MetroLine> metroLines;
    MetroTimeTable metroTimeTable;
    List<MetroTimeTable> metroTimeTables;
    MetroTrip metroTrip;

    @Mock
    MetroStationRepository metroStationRepository;

    @Mock
    MetroLineRepository metroLineRepository;

    @Mock
    MetroTimeTableRepository metroTimeTableRepository;

    @InjectMocks
    @Autowired
    KmrlAdminServiceImpl kmrlAdminService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);

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
    public void getAllStationsTest() throws Exception {
        when(metroStationRepository.findAllByDisplayNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(metroStations);
        Assert.assertNotNull(kmrlAdminService.getAllStations(0, 10, "%"));
    }

    @Test
    public void getAllRoutesTest() throws Exception {
        when(metroLineRepository.findAllByDisplayNameContainingIgnoreCaseOrLineCodeContainingIgnoreCase(any(String.class), any(String.class), any(Pageable.class))).thenReturn(metroLines);
        Assert.assertNotNull(kmrlAdminService.getAllRoutes(0, 10, "%"));
    }

    @Test
    public void getAllTimetablesTest() throws Exception {
        when(metroTimeTableRepository.findAllByStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(any(String.class), any(String.class), any(Pageable.class))).thenReturn(metroTimeTables);
        Assert.assertNotNull(kmrlAdminService.getAllTimetable(0, 10, "%"));
    }

    @Test
    public void updateStationTest() throws Exception {
        MetroStationAdminDTO metroStationAdminDTO = new MetroStationAdminDTO();
        metroStationAdminDTO.setMetroLine(Arrays.asList("blue"));
        metroStationAdminDTO.setStationId("123");
        metroStationAdminDTO.setStationCodeUp("ALVup");
        metroStationAdminDTO.setStationCodeDn("ALVdn");
        metroStationAdminDTO.setDisplayName("ALUVAA");
        metroStationAdminDTO.setDistance(23.4);
        metroStationAdminDTO.setLatitude(76.3);
        metroStationAdminDTO.setLongitude(77.4);
        when(metroStationRepository.findByStationId(any(String.class))).thenReturn(metroStation);
        when(metroStationRepository.save(any(MetroStation.class))).thenReturn(metroStation);
        kmrlAdminService.updateStation(metroStationAdminDTO);
        Assert.assertEquals("ALUVAA", metroStation.getDisplayName());
    }

    @Test
    public void updateRoutenTest() throws Exception {
        MetroLineAdminDTO metroLineAdminDTO = new MetroLineAdminDTO();
        MetroStationMappedDTO metroStationMappedDTO = new MetroStationMappedDTO();
        metroStationMappedDTO.setStationId("123");
        metroStationMappedDTO.setDisplayName("ALUVI");
        List<MetroStationMappedDTO> metroStationMappedDTOS = new ArrayList<>();
        metroStationMappedDTOS.add(metroStationMappedDTO);
        metroLineAdminDTO.setStations(metroStationMappedDTOS);
        metroLineAdminDTO.setLineId("123");
        metroLineAdminDTO.setLineCode("Blue");
        metroLineAdminDTO.setDisplayName("White");

        when(metroLineRepository.findByLineId(any(String.class))).thenReturn(metroLine);
        when(metroLineRepository.save(any(MetroLine.class))).thenReturn(metroLine);
        kmrlAdminService.updateRoute(metroLineAdminDTO);
        Assert.assertEquals("White", metroLine.getDisplayName());
    }
}
