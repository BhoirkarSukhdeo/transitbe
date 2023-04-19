package com.axisbank.transit.kmrl.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.kmrl.model.DAO.*;
import com.axisbank.transit.kmrl.repository.MetroLineRepository;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableTypeRepository;
import com.axisbank.transit.kmrl.repository.MetroTripRepository;
import com.axisbank.transit.kmrl.service.impl.KmrlDataUploadServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.stream.Collectors;

import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.PUBLISHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class KmrlDataUploadServiceTest extends BaseTest {
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

    @Mock
    MetroStationRepository metroStationRepository;

    @Mock
    MetroLineRepository metroLineRepository;

    @Mock
    RedisClient redisClient;

    @Mock
    TimeTableService timeTableService;

    @Mock
    MetroTimeTableTypeRepository metroTimeTableTypeRepository;

    @Mock
    MetroTripRepository metroTripRepository;

    @InjectMocks
    @Autowired
    KmrlDataUploadServiceImpl kmrlDataUploadService;

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

        timeTableType = new MetroTimeTableType();
        timeTableType.setTimeTableName("table1");
        timeTableType.setActive(true);
        timeTableType.setActiveDays("Sunday");
        timeTableType.setCurrentStatus("created");
        timeTableType.setMtTimetableId("mtTimetable123");

        timeTableTypes = new ArrayList<>();
        timeTableTypes.add(timeTableType);

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

        metroTimeTableVerList = new ArrayList<>();
        metroTimeTableVerList.add(metroTimeTableVer);

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
    }

    @Test
    public void uploadStationsTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Stations");
        Object[][] stationData = {
                {"displayName", "distance", "latitude", "longitude", "stationCodeUp", "stationCodeDn", "selStationId", "selStationCode"},
                {"Aluva", "0", "10.916791", "76.568634", "STA_COD_3105T_UP", "STA_COD_3106T_UP", 1, "ALVA"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        when(metroStationRepository.saveAll(anyList())).thenReturn(metroStations);
        kmrlDataUploadService.uploadStations(workbook, "Metro Stations");
    }

    @Test
    public void uploadStationsTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Stations");
        Object[][] stationData = {
                {"displayName", "distance", "latitude", "longitude", "stationCodeUp", "stationCodeDn", "selStationId", "selStationCode"},
                {"Muttom", "0", "10.416791", "76.068634", "STA_COD_3509T_DN", "STA_COD_3512T_BH", 1, "MUTT"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        when(metroStationRepository.saveAll(anyList())).thenReturn(metroStations);
        kmrlDataUploadService.uploadStations(workbook, "Metro Stations");
    }

    @Test
    public void uploadStationsTest3() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Stations");
        Object[][] stationData = {
                {"displayName", "distance", "latitude", "longitude", "stationCodeUp", "stationCodeDn", "selStationId", "selStationCode"},
                {"Aluva", "0", "10.916791", "", "STA_COD_3105T_UP", "STA_COD_3106T_UP", 1, "ALVA"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        when(metroStationRepository.saveAll(anyList())).thenReturn(metroStations);
        kmrlDataUploadService.uploadStations(workbook, "Metro Stations");
    }

    @Test
    public void uploadRoutesTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Routes");
        Object[][] stationData = {
                {"displayName", "lineCode"},
                {"Blue", "blue"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroLineRepository.findAll()).thenReturn(metroLines);
        when(metroLineRepository.saveAll(anyList())).thenReturn(metroLines);
        kmrlDataUploadService.uploadRoutes(workbook, "Metro Routes");
    }

    @Test
    public void uploadRoutesTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Routes");
        Object[][] stationData = {
                {"displayName", "lineCode"},
                {"Red", "red"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroLineRepository.findAll()).thenReturn(metroLines);
        when(metroLineRepository.saveAll(anyList())).thenReturn(metroLines);
        kmrlDataUploadService.uploadRoutes(workbook, "Metro Routes");
    }

    @Test
    public void uploadRoutesTest3() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Routes");
        Object[][] stationData = {
                {"displayName", "lineCode"},
                {"Blue", ""},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroLineRepository.findAll()).thenReturn(metroLines);
        when(metroLineRepository.saveAll(anyList())).thenReturn(metroLines);
        kmrlDataUploadService.uploadRoutes(workbook, "Metro Routes");
    }

    @Test
    public void uploadStationRoutesTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Station Routes");
        Object[][] stationData = {
                {"selStationCode", "Line Code"},
                {"ALVA", "blue"},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroLineRepository.findAll()).thenReturn(metroLines);
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        when(metroStationRepository.saveAll(anyList())).thenReturn(metroStations);
        kmrlDataUploadService.uploadStationRoutes(workbook, "Metro Station Routes");
    }

    @Test
    public void uploadStationRoutesTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Metro Station Routes");
        Object[][] stationData = {
                {"selStationCode", "Line Code"},
                {"ALVA", ""},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(metroLineRepository.findAll()).thenReturn(metroLines);
        when(metroStationRepository.findAll()).thenReturn(metroStations);
        when(metroStationRepository.saveAll(anyList())).thenReturn(metroStations);
        kmrlDataUploadService.uploadStationRoutes(workbook, "Metro Station Routes");
    }

    @Test
    public void XMLTimetableTest() throws Exception {
        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<ROOT>\n" +
                "    <Versions>\n" +
                "        <ID Name=\"OGT-G\" Version=\"OGT-G-05.16\"></ID>\n" +
                "        <ID Name=\"OGT-ATS-INTERFACE\" Version=\"3.0\"></ID>\n" +
                "        <ID Name=\"ODPT_APPLICATION_AREA\" Version=\"UEVOL_REG_KMRL_2.6.1_0072\"></ID>\n" +
                "    </Versions>\n" +
                "    <TITLE>Schedule file</TITLE>\n" +
                "    <SCHEDULE NAME=\"14WPETTwef14sep\" COMMENT=\"\">\n" +
                "        <TRIPS>\n" +
                "            <TRIP NUMBER=\"245\" TRIP_ID=\"0245\" SERVICE_ID=\"05\" DIRECTION=\"LEFT\" ENTRY_TIME=\"06:46:55\" DISTANCE=\"4611\" TRAIN_CLASS=\"TRFC_EMU\" MISSION_TYPE=\"Passenger\" RUNNING_MODE=\"Regulated\" PREVIOUS_NUMBER=\"\" NEXT_NUMBER=\"3\">\n" +
                "                <STOP TOP=\"STA_COD_3509T_DN\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_COD_3509T_DN_STA_PF_DN_AATK\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_AATK\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_AATK_STA_PF_DN_CPPY\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_CPPY\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_CPPY_STA_PF_DN_PNCU\" RUNTIME=\"86\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_PNCU\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_PNCU_STA_COD_3106T_UP\" RUNTIME=\"132\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_COD_3106T_UP\" DWELLTIME=\"0\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "            </TRIP>\n" +
                "            <TRIP NUMBER=\"258\" TRIP_ID=\"0258\" SERVICE_ID=\"05\" DIRECTION=\"RIGHT\" ENTRY_TIME=\"12:00:06\" DISTANCE=\"4616\" TRAIN_CLASS=\"TRFC_EMU\" MISSION_TYPE=\"Passenger\" RUNNING_MODE=\"Regulated\" PREVIOUS_NUMBER=\"62\" NEXT_NUMBER=\"\">\n" +
                "                <STOP TOP=\"STA_COD_3105T_UP\" DWELLTIME=\"180\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_COD_3105T_UP_STA_PF_UP_PNCU\" RUNTIME=\"137\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_PNCU\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_PNCU_STA_PF_UP_CPPY\" RUNTIME=\"86\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_CPPY\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_CPPY_STA_PF_UP_AATK\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_AATK\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_AATK_STA_COD_3512T_BH\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_COD_3512T_BH\" DWELLTIME=\"0\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "            </TRIP>\n" +
                "        </TRIPS>\n" +
                "    </SCHEDULE>\n" +
                "</ROOT>";

        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        PowerMockito.when(CommonUtils.addSecondsToTime(any(String.class), any(String.class), any(Integer.class))).thenReturn("06:47:25");
        PowerMockito.when(CommonUtils.addSecondsToTime(any(String.class), any(Integer.class))).thenReturn("06:47:25");
        when(timeTableService.getMetroStation(any(String.class))).thenReturn(metroStation);
        when(metroTimeTableTypeRepository.save(any(MetroTimeTableType.class))).thenReturn(timeTableType);
        kmrlDataUploadService.processXmlTimetable(xmlData, "table1", "Sunday");
    }

    @Test
    public void enableTimetableTest() throws Exception {
        when(metroTimeTableTypeRepository.findByMtTimetableIdAndCurrentStatus(any(String.class), any(String.class))).thenReturn(timeTableType);
        when(metroTimeTableTypeRepository.findAllByIsActive(any(Boolean.class))).thenReturn(timeTableTypes);
        when(metroTimeTableTypeRepository.saveAll(anyList())).thenReturn(timeTableTypes);
        List<MetroTrip> metroTripList = new ArrayList<>();
        doNothing().when(metroTripRepository).deleteAll();
        when(metroTripRepository.saveAll(anyList())).thenReturn(metroTripList);
        when(metroTimeTableTypeRepository.save(any(MetroTimeTableType.class))).thenReturn(timeTableType);
        kmrlDataUploadService.enableTimetable("123");
    }

    @Test
    public void getUploadStatusTest() throws Exception {
        UploadDataStatus uploadDataStatus = new UploadDataStatus();
        uploadDataStatus.setCurrentStatus("completed");
        uploadDataStatus.setStartTime("07:20:00");
        uploadDataStatus.setErrors(null);
        uploadDataStatus.setLabel("Bus Upload");
        uploadDataStatus.setProcessed(null);
        PowerMockito.when(CommonUtils.convertJsonStringToObject(any(String.class), any())).thenReturn(uploadDataStatus);
        when(redisClient.getValue(any(String.class))).thenReturn("test");
        Assert.assertEquals("completed", kmrlDataUploadService.getUploadStatus().getCurrentStatus());
    }
}
