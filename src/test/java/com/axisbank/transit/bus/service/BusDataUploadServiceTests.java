package com.axisbank.transit.bus.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.bus.model.DAO.*;
import com.axisbank.transit.bus.repository.*;
import com.axisbank.transit.bus.service.impl.BusDataUploadServiceImpl;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class BusDataUploadServiceTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    BusStation busStation1;
    BusStation busStation2;
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
    private BusStationRepository busStationRepository;

    @Mock
    private BusRouteRepository busRouteRepository;

    @Mock
    private BusFareRepository busFareRepository;

    @Mock
    private BusTimeTableRepository busTimeTableRepository;
    @Mock
    private BusTimetableTypeRepository busTimetableTypeRepository;
    @Mock
    private BusFareTypeRepository busFareTypeRepository;
    @Mock
    RedisClient redisClient;

    @InjectMocks
    @Autowired
    BusDataUploadServiceImpl busDataUploadService;

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

        busFare = new BusFare();
        busFare.setFareId("fare123");
        busFare.setFare(18.0);
        busFare.setToBusStation(busStation1);
        busFare.setFromBusStation(busStation2);
        busFare.setBusRoute(busRoute);

        busFares = new ArrayList<>();
        busFares.add(busFare);

        time = java.sql.Time.valueOf("06:20:00");
        busTimeTable = new BusTimeTable();
        busTimeTable.setTimeTableId("timetable123");
        busTimeTable.setArivalTime(time);
        busTimeTable.setDepartureTime(time);
        busTimeTable.setBusRoute(busRoute);
        busTimeTable.setBusStation(busStation1);
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
        busTimeTableVer.setBusStation(busStation1);
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
        busFareVer.setToBusStation(busStation1);
        busFareVer.setFromBusStation(busStation2);
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
    public void uploadStationsTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Stations");
        Object[][] stationData = {
                {"displayName", "stationCode", "latitude", "longitude"},
                {"ELOOR MACHANAM", "ELOOR MACHANAM", 10.916791, 76.568634},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busStationRepository.saveAll(anyList())).thenReturn(busStations);
        busDataUploadService.uploadStations(workbook, "Bus Stations");
    }

    @Test
    public void uploadStationsTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Stations");
        Object[][] stationData = {
                {"displayName", "stationCode", "latitude", "longitude"},
                {"IAC", "IAC", 10.916791, 76.568634},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busStationRepository.saveAll(anyList())).thenReturn(busStations);
        busDataUploadService.uploadStations(workbook, "Bus Stations");
    }

    @Test
    public void uploadStationsTest3() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Stations");
        Object[][] stationData = {
                {"displayName", "stationCode", "latitude", "longitude"},
                {"", "ELOOR MACHANAM", 10.916791, 76.568634},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busStationRepository.saveAll(anyList())).thenReturn(busStations);
        busDataUploadService.uploadStations(workbook, "Bus Stations");
    }

    @Test
    public void uploadBusRoutesTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Routes");
        Object[][] stationData = {
                {"Route Name", "RouteNameUP", "RouteName Down", "Association", "Vehicle Number", "bus_type"},
                {"ELOOR MACHANAM - W ISLAND VIA MENAKA","ELOOR MACHANAM - W ISLAND VIA MENAKA","W ISLAND - ELOOR MACHANAM VIA MENAKA","KOCHIWHEELZ","KL-01-AQ-7722","normal"},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busRouteRepository.saveAll(anyList())).thenReturn(busRoutes);
        busDataUploadService.uploadBusRoutes(workbook, "Bus Routes");
    }

    @Test
    public void uploadBusRoutesTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Routes");
        Object[][] stationData = {
                {"Route Name", "RouteNameUP", "RouteName Down", "Association", "Vehicle Number", "bus_type"},
                {"ELOOR MACHANAM - W ISLAND VIA MENAKA","ELOOR MACHANAM - W ISLAND VIA MENAKA","W ISLAND - ELOOR MACHANAM VIA MENAKA","KOCHIWHEELZ","KL-01-AQ-7723","normal"},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busRouteRepository.saveAll(anyList())).thenReturn(busRoutes);
        busDataUploadService.uploadBusRoutes(workbook, "Bus Routes");
    }

    @Test
    public void uploadBusRoutesTest3() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Routes");
        Object[][] stationData = {
                {"Route Name", "RouteNameUP", "RouteName Down", "Association", "Vehicle Number", "bus_type"},
                {"","ELOOR MACHANAM - W ISLAND VIA MENAKA","W ISLAND - ELOOR MACHANAM VIA MENAKA","KOCHIWHEELZ","KL-01-AQ-7722","normal"},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busRouteRepository.saveAll(anyList())).thenReturn(busRoutes);
        busDataUploadService.uploadBusRoutes(workbook, "Bus Routes");
    }

    @Test
    public void uploadBusStationRoutesTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Station Route");
        Object[][] stationData = {
                {"Route Name", "Vehicle Number", "Station Code"},
                {"ELOOR MACHANAM - W ISLAND VIA MENAKA","KL-01-AQ-7722","PCC"},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busStationRepository.saveAll(anyList())).thenReturn(busStations);
        busDataUploadService.uploadBusStationRoutes(workbook, "Bus Station Route");
    }

    @Test
    public void uploadBusStationRoutesTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Station Route");
        Object[][] stationData = {
                {"Route Name", "Vehicle Number", "Station Code"},
                {"","KL-01-AQ-7722","PCC"},
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
                }
                columnCount++;
            }
            rowCount++;
        }
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busStationRepository.saveAll(anyList())).thenReturn(busStations);
        busDataUploadService.uploadBusStationRoutes(workbook, "Bus Station Route");
    }

    @Test
    public void uploadBusFaresTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Fare");
        Object[][] stationData = {
                {"Vehicle","Route Name","Stages","ELOOR MACHANAM","PCC"},
                {"KL-01-AQ-7722","ELOOR MACHANAM - W ISLAND VIA MENAKA","ELOOR MACHANAM",0,8},
                {"KL-01-AQ-7722","ELOOR MACHANAM - W ISLAND VIA MENAKA","PCC",8,0},
        };
        int rowCount = 0;

        for (Object[] station : stationData) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : station) {
                Cell cell = row.createCell(columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
                columnCount++;
            }
            rowCount++;
        }
        when(busFareTypeRepository.findByBusFareName(any(String.class))).thenReturn(busFareType);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busFareTypeRepository.save(any(BusFareType.class))).thenReturn(busFareType);
        busDataUploadService.uploadBusFares(workbook, "Bus Fare", "fare123", "Sunday");
    }

    @Test
    public void uploadBusFaresTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Fare");
        Object[][] stationData = {
                {"Vehicle","Route Name","Stages","ELOOR MACHANAM","PCC"},
                {"KL-01-AQ-7722","ELOOR MACHANAM - W ISLAND VIA MENAKA","ELOOR MACHANAM",0,8},
                {"KL-01-AQ-7722","","PCC",8,0},
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
        when(busFareTypeRepository.findByBusFareName(any(String.class))).thenReturn(busFareType);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busFareTypeRepository.save(any(BusFareType.class))).thenReturn(busFareType);
        busDataUploadService.uploadBusFares(workbook, "Bus Fare", "fare123", "Sunday");
    }

    @Test
    public void uploadBusTimetableTest() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Timetable");
        Time time1 = Time.valueOf("07:30:00");
        Object[][] stationData = {
                {"Route Name","Vehicle","Route Type","Stages","TRIP 1", "TRIP2"},
                {"ELOOR MACHANAM - W ISLAND VIA MENAKA","KL-01-AQ-7722","Up","ELOOR MACHANAM",time1, time1},
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
                } else if (field instanceof LocalDateTime) {
                    cell.setCellValue((LocalDateTime) field);
                } else if (field instanceof Time) {
                    cell.setCellValue((Time) field);
                }
                columnCount++;
            }
            rowCount++;
        }

        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busTimetableTypeRepository.findByTimeTableName(any(String.class))).thenReturn(busTimeTableType);
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busTimetableTypeRepository.save(any(BusTimeTableType.class))).thenReturn(busTimeTableType);
        busDataUploadService.uploadBusTimetable(workbook, "Bus Timetable", "fare123", "Sunday");
    }

    @Test
    public void uploadBusTimetableTest2() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Timetable");
        Time time1 = Time.valueOf("07:30:00");
        Object[][] stationData = {
                {"Route Name","Vehicle","Route Type","Stages","TRIP 1", "TRIP2"},
                {"ELOOR MACHANAM - W ISLAND VIA MENAKA","KL-01-AQ-7722","Up","ELOOR MACHANAM","", time1},
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
                } else if (field instanceof LocalDateTime) {
                    cell.setCellValue((LocalDateTime) field);
                } else if (field instanceof Time) {
                    cell.setCellValue((Time) field);
                }
                columnCount++;
            }
            rowCount++;
        }

        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busTimetableTypeRepository.findByTimeTableName(any(String.class))).thenReturn(busTimeTableType);
        when(busRouteRepository.findAll()).thenReturn(busRoutes);
        when(busStationRepository.findAll()).thenReturn(busStations);
        when(busTimetableTypeRepository.save(any(BusTimeTableType.class))).thenReturn(busTimeTableType);
        busDataUploadService.uploadBusTimetable(workbook, "Bus Timetable", "fare123", "Sunday");
    }

    @Test
    public void enableBusTimetableTest() throws Exception {
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busTimetableTypeRepository.findByBusTimetableIdAndCurrentStatus(any(String.class), any(String.class))).thenReturn(busTimeTableType);
        when(busTimetableTypeRepository.findAllByIsActive(any(Boolean.class))).thenReturn(timeTableTypes);
        when(busTimetableTypeRepository.saveAll(anyList())).thenReturn(timeTableTypes);
        doNothing().when(busTimeTableRepository).deleteAll();
        when(busTimeTableRepository.saveAll(anyList())).thenReturn(busTimeTables);
        when(busTimetableTypeRepository.save(any(BusTimeTableType.class))).thenReturn(busTimeTableType);
        busDataUploadService.enableBusTimetable("123");
    }

    @Test
    public void enableFareTest() throws Exception {
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(String.class))).thenReturn("test");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        when(busFareTypeRepository.findByBusFareTypeIdAndCurrentStatus(any(String.class), any(String.class))).thenReturn(busFareType);
        when(busFareTypeRepository.findAllByIsActive(any(Boolean.class))).thenReturn(busFareTypes);
        when(busFareTypeRepository.saveAll(anyList())).thenReturn(busFareTypes);
        doNothing().when(busFareRepository).deleteAll();
        when(busFareRepository.saveAll(anyList())).thenReturn(busFares);
        when(busFareTypeRepository.save(any(BusFareType.class))).thenReturn(busFareType);
        busDataUploadService.enableFare("123");
    }

}
