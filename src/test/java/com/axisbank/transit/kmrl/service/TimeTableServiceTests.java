package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.core.shared.utils.WorkFlowUtil;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.kmrl.model.DAO.*;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableDTOInterface;
import com.axisbank.transit.kmrl.repository.MetroTimeTableRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableTypeRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableVerRepository;
import com.axisbank.transit.kmrl.service.impl.TimeTableServiceImpl;
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

import static com.axisbank.transit.core.shared.utils.CommonUtils.getTimeDiff;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommonUtils.class, WorkFlowUtil.class})
public class TimeTableServiceTests extends BaseTest {
    MetroStation metroStation;
    List<MetroStation> metroStations;
    MetroLine metroLine;
    List<MetroLine> metroLines;
    MetroTimeTableType timeTableType;
    List<MetroTimeTableType> timeTableTypes;
    MetroTripVer metroTripVer;
    List<MetroTripVer> trips;
    MetroTimeTableVer metroTimeTableVer;
    List<MetroTimeTableVer> metroTimeTableVerList;
    MetroTimeTable metroTimeTable;
    List<MetroTimeTable> metroTimeTables;
    MetroTrip metroTrip;

    @Mock
    StationService stationService;

    @Mock
    MetroTimeTableRepository metroTimeTableRepository;

    @Mock
    MetroTimeTableTypeRepository metroTimeTableTypeRepository;

    @Mock
    BookTicketService bookTicketService;

    @Mock
    RedisClient redisClient;
    @Mock
    MetroTimeTableVerRepository metroTimeTableVerRepository;
    @InjectMocks
    @Autowired
    TimeTableServiceImpl timeTableService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.mockStatic(WorkFlowUtil.class);

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

        timeTableType = new MetroTimeTableType();
        timeTableType.setTimeTableName("table1");
        timeTableType.setActive(true);
        timeTableType.setActiveDays("Sunday");
        timeTableType.setCurrentStatus("created");
        timeTableType.setMtTimetableId("mtTimetable123");

        timeTableTypes = new ArrayList<>();
        timeTableTypes.add(timeTableType);

        metroTripVer = new MetroTripVer();
        metroTripVer.setMtTripId("123");
        metroTripVer.setDirection("Up");
        metroTripVer.setServiceId("234");
        metroTripVer.setTripId("1");
        metroTripVer.setStartTime(Time.valueOf("09:30:00"));
        metroTripVer.setNextNumber("2");
        metroTripVer.setPrevNumber("1");
        metroTripVer.setTimeTables(metroTimeTableVerList);

        trips = new ArrayList<>();
        trips.add(metroTripVer);
        timeTableType.setTrips(trips);

        metroTimeTableVer = new MetroTimeTableVer();
        metroTimeTableVer.setArivalTime(Time.valueOf("02:30:00"));
        metroTimeTableVer.setDepartureTime(Time.valueOf("03:00:00"));
        metroTimeTableVer.setTimeTableId("123");
        metroTimeTableVer.setDwelltime(23);
        metroTimeTableVer.setMetroLineName("Blue");
        metroTimeTableVer.setSrNum(1);
        metroTimeTableVer.setStation(metroStation);
        metroTimeTableVer.setPrevRuntime(20);
        metroTimeTableVer.setTotalRuntime(30);
        metroTimeTableVer.setTrip(metroTripVer);

        metroTimeTableVerList = new ArrayList<>();
        metroTimeTableVerList.add(metroTimeTableVer);

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
    public void getAllMetroTimetablesTest() throws Exception {
        when(metroTimeTableTypeRepository.findAll()).thenReturn(timeTableTypes);
        Assert.assertNotNull(timeTableService.getAllMetroTimetables());
    }

    @Test
    public void getMetroTimetableByTypeTest() throws Exception {
        when(metroTimeTableVerRepository.findAllByTrip_TimeTableType_MtTimetableIdAndStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(metroTimeTableVerList);
        Assert.assertNotNull(timeTableService.getMetroTimetableByType("123", 0, 10, "abc"));
    }

    @Test
    public void updateTimeTableStatusTest() throws Exception {
        when(metroTimeTableTypeRepository.findByMtTimetableId(any(String.class))).thenReturn(timeTableType);
        PowerMockito.when(WorkFlowUtil.verifyWorkFlow(any(String.class), any(String.class))).thenReturn(true);
        when(metroTimeTableTypeRepository.save(any(MetroTimeTableType.class))).thenReturn(timeTableType);
        timeTableService.updateTimeTableStatus("123", "abc");
    }

    @Test
    public void getMetroDetailsTest() throws Exception {
        when(stationService.getStationById(any(String.class))).thenReturn(metroStation);
        when(metroTimeTableRepository.findAllByStationAndArivalTimeGreaterThanEqualAndArivalTimeLessThanEqual(any(MetroStation.class), any(Time.class), any(Time.class))).thenReturn(metroTimeTables);
        when(metroTimeTableRepository.findByStationAndTripAndSrNumGreaterThan(any(MetroStation.class), any(MetroTrip.class), any(Long.class))).thenReturn(metroTimeTable);
        when(metroTimeTableRepository.findAllByTripAndSrNumGreaterThanAndSrNumLessThan(any(MetroTrip.class), any(Long.class), any(Long.class))).thenReturn(metroTimeTables);
        PowerMockito.when(CommonUtils.getCurrentDateTime(any(String.class))).thenReturn("07:30:00");
        PowerMockito.when(CommonUtils.addSecondsToTime("07:30:00", 15 * 60)).thenReturn("08:30:00");
        Assert.assertNotNull(timeTableService.getMetroDetails("abc", "xyz"));

    }

    @Test
    public void getMetroRouteDetailsTest() throws Exception {
        String timings = "02:30:00";
        when(redisClient.getValue(any(String.class))).thenReturn(timings);
        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        PowerMockito.when(CommonUtils.convertJsonStringToObject(timings, JourneyModeDetails.class)).thenReturn(journeyModeDetails);
        when(stationService.getStationById(any(String.class))).thenReturn(metroStation);
        when(metroTimeTableRepository.findAllByStationAndArivalTimeGreaterThanEqualAndArivalTimeLessThanEqual(any(MetroStation.class), any(Time.class), any(Time.class))).thenReturn(metroTimeTables);
        when(metroTimeTableRepository.findByStationAndTripAndSrNumGreaterThan(any(MetroStation.class), any(MetroTrip.class), any(Long.class))).thenReturn(metroTimeTable);
        when(metroTimeTableRepository.findAllByTripAndSrNumGreaterThanAndSrNumLessThan(any(MetroTrip.class), any(Long.class), any(Long.class))).thenReturn(metroTimeTables);
        PowerMockito.when(CommonUtils.getCurrentDateTime(any(String.class))).thenReturn("07:30:00");
        PowerMockito.when(CommonUtils.addSecondsToTime("07:30:00", 15 * 60)).thenReturn("08:30:00");

        MetroTimeTableDTOInterface metroTimeTableDTOInterface = new MetroTimeTableDTOInterface() {
            @Override
            public long getSource_Sr_Num() {
                return 23;
            }

            @Override
            public long getDest_Sr_Num() {
                return 45;
            }

            @Override
            public long getMetro_Trip_Id() {
                return 12;
            }

            @Override
            public String getArrival_Time() {
                return "07:30:00";
            }
        };
        List<MetroTimeTableDTOInterface> upcomingTrips = new ArrayList<>();
        upcomingTrips.add(metroTimeTableDTOInterface);

        when(metroTimeTableRepository.findAllTripsBetweenStations(any(Long.class), any(Long.class), any(String.class))).thenReturn(upcomingTrips);
        doNothing().when(redisClient).setValue(any(String.class), any(String.class));
        PowerMockito.when(CommonUtils.convertObjectToJsonString(anyList())).thenReturn("abc");
        PowerMockito.when(CommonUtils.getTimeDiff(any(Time.class), any(Time.class))).thenReturn(Long.valueOf(300));

        MetroTimeTable metroTimeTable2 = new MetroTimeTable();
        metroTimeTable2.setArivalTime(Time.valueOf("02:30:00"));
        metroTimeTable2.setDepartureTime(Time.valueOf("03:30:00"));
        metroTimeTable2.setTimeTableId("timetable123");
        metroTimeTable2.setDwelltime(300);
        metroTimeTable2.setMetroLineName("blue");
        metroTimeTable2.setPrevRuntime(200);
        metroTimeTable2.setSrNum(2);
        metroTimeTable2.setStation(metroStation);
        metroTimeTable2.setTotalRuntime(500);
        metroTimeTable2.setTrip(metroTrip);
        metroTimeTables.add(metroTimeTable2);
        when(metroTimeTableRepository.findAllByTripIdAndSrNumBetweenOrderBySrNum(any(Long.class), any(Long.class), any(Long.class))).thenReturn(metroTimeTables);

        Assert.assertNotNull(timeTableService.getMetroRouteDetails("abc", "xyz", "02:30:00"));
    }

}
