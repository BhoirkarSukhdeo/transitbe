package com.axisbank.transit.bus.service.impl;

import com.axisbank.transit.bus.model.DAO.*;
import com.axisbank.transit.bus.repository.*;
import com.axisbank.transit.bus.service.BusDataUploadService;
import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.*;

import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.CREATED;
import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.PUBLISHED;

@Service
@Slf4j
public class BusDataUploadServiceImpl implements BusDataUploadService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private BusStationRepository busStationRepository;

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private BusFareRepository busFareRepository;

    @Autowired
    private BusTimeTableRepository busTimeTableRepository;
    @Autowired
    private BusTimetableTypeRepository busTimetableTypeRepository;
    @Autowired
    private BusFareTypeRepository busFareTypeRepository;
    @Autowired
    RedisClient redisClient;

    Map<String, BusStation> busStationMap = new HashMap<>();
    Map<String, BusRoute> busRouteMap = new HashMap<>();
    Map<String, BusRoute> busRouteMapToRemove = new HashMap<>();
    Map<String, BusFareVer> busFareMap = new HashMap<>();
    Map<String, BusTimeTableVer> busTimeTableMap = new HashMap<>();
    private final String redisKey = "BUS_DATA_UPLOAD";

    @Async
    @Override
    @Transactional
    public void uploadStations(Workbook workbook, String sheetName) throws Exception {
        List<BusStation> stations = new ArrayList<>();
        Sheet firstSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = firstSheet.iterator();
        UploadDataStatus status = new UploadDataStatus("Bus station data upload", new HashSet<>(),new HashSet<>(),
                "Started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        List<BusStation> storedStations = busStationRepository.findAll();
        for (BusStation busStation : storedStations) {
            busStationMap.put(busStation.getStationCode(), busStation);
        }
        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                String stationCode = checkNullEmpty(nextRow.getCell(1).toString().trim());
                String displayName = checkNullEmpty(nextRow.getCell(0).toString());
                String latitude = checkNullEmpty(nextRow.getCell(2).toString());
                String longitude = checkNullEmpty(nextRow.getCell(3).toString());
                BusStation busStation = null;
                if (busStationMap.get(stationCode) != null) {
                    log.debug("Station code already present: " + stationCode);
                    busStation = busStationMap.get(stationCode);
                    busStation.setLatitude(Double.parseDouble(latitude));
                    busStation.setLongitude(Double.parseDouble(longitude));
                    busStation.setDisplayName(displayName);
                } else {
                    busStation = new BusStation();
                    busStation.setStationId(CommonUtils.generateRandomString(30));
                    busStation.setDisplayName(displayName);
                    busStation.setStationCode(stationCode);
                    busStation.setLatitude(Double.parseDouble(latitude));
                    busStation.setLongitude(Double.parseDouble(longitude));
                }
                stations.add(busStation);
                busStationMap.put(stationCode, busStation);
                status.getProcessed().add(stationCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            busStationRepository.saveAll(stations);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);
        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    @Async
    @Override
    @Transactional
    public void uploadBusRoutes(Workbook workbook, String sheetName) throws Exception {
        List<BusRoute> routes = new ArrayList<>();
        Sheet firstSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = firstSheet.iterator();
        UploadDataStatus status = new UploadDataStatus("Bus station routes data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        List<BusRoute> storedRuotes = busRouteRepository.findAll();
        for (BusRoute busRoute : storedRuotes) {
            busRouteMap.put(busRoute.getRouteName() + "-" + busRoute.getVehicleNumber(), busRoute);
        }
        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                String routeName = checkNullEmpty(nextRow.getCell(0).toString().trim());
                String routeNameUp = checkNullEmpty(nextRow.getCell(1).toString().trim());
                String routeNameDown = checkNullEmpty(nextRow.getCell(2).toString().trim());
                String busType = checkNullEmpty(nextRow.getCell(5).toString().trim());
                String routeCode = routeName + "-" + nextRow.getCell(4).toString().trim();
                BusRoute busRoute = null;
                if (busRouteMap.get(routeCode) != null) {
                    log.debug("Route code already present in database: " + routeCode);
                    busRoute = busRouteMap.get(routeCode);
                    busRoute.setAssociation(nextRow.getCell(3).toString());
                    busRoute.setBusType(busType);
                } else {
                    busRoute = new BusRoute();
                    busRoute.setRouteId(CommonUtils.generateRandomString(30));
                    busRoute.setRouteName(routeName);
                    busRoute.setRouteNameUp(routeNameUp);
                    busRoute.setRouteNameDown(routeNameDown);
                    busRoute.setAssociation(nextRow.getCell(3).toString());
                    busRoute.setVehicleNumber(nextRow.getCell(4).toString());
                    busRoute.setBusType(busType);
                    busRoute.setRouteCode(routeCode);
                }

                routes.add(busRoute);
                busRouteMap.put(routeCode, busRoute);
                status.getProcessed().add(routeCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            busRouteRepository.saveAll(routes);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);
        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    @Async
    @Override
    @Transactional
    public void uploadBusStationRoutes(Workbook workbook, String sheetName) throws Exception {
        List<BusStation> stations = new ArrayList<>();
        Sheet firstSheet = workbook.getSheet(sheetName);
        UploadDataStatus status = new UploadDataStatus("Bus station routes map data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        if(firstSheet==null){
            StringBuilder availableSheets= new StringBuilder();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++)
            {
                Sheet sheet = workbook.getSheetAt(i);
                availableSheets.append(",").append(sheet.getSheetName());
            }
            log.error("Sheet Not Available:{}, Available sheets:{}", sheetName, availableSheets.toString());
            status.getErrors().add(" Given Sheet Not Available:"+sheetName+", Available sheets:"+availableSheets.toString());
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
            return;
        }
        Iterator<Row> iterator = firstSheet.iterator();
        List<BusStation> storedStations = busStationRepository.findAll();
        for (BusStation busStation : storedStations) {
            busStationMap.put(busStation.getStationCode(), busStation);
        }

        List<BusRoute> storedRuotes = busRouteRepository.findAll();
        for (BusRoute busRoute : storedRuotes) {
            String vehicle = busRoute.getVehicleNumber() != null ? busRoute.getVehicleNumber() : "";
            busRouteMap.put(busRoute.getRouteNameUp() + "-" + vehicle, busRoute);
            busRouteMap.put(busRoute.getRouteNameDown() + "-" + vehicle, busRoute);
        }

        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            Set<String> distictRouteCodes = new HashSet<>();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                String routeName = checkNullEmpty(nextRow.getCell(0).toString());
                String vehicle = nextRow.getCell(1)!=null?nextRow.getCell(1).toString().trim():"";
                String routeCode = routeName + "-" + vehicle;
                String stationCode = checkNullEmpty(nextRow.getCell(2).toString().trim());
                BusRoute busRoute = null;
                BusStation busStation = null;
                if (busRouteMap.get(routeCode) != null) {
                    distictRouteCodes.add(routeCode);
                    busRoute = busRouteMap.get(routeCode);
                } else {
                    status.getErrors().add("Route: "+routeCode);
                    setUploadStatusRedis(status);
                    log.debug("Route code does not present in DB"+routeCode);
                    continue;
                }
                if (busStationMap.get(stationCode) != null) {
                    busStation = busStationMap.get(stationCode);
                } else {
                    status.getErrors().add("Station: "+stationCode);
                    setUploadStatusRedis(status);
                    log.debug("Station code does not present in DB"+stationCode);
                    continue;
                }
                Set<BusRoute> busRouteSet = busStation.getRouteSet();
                busRouteSet.add(busRoute);
                busStation.setRouteSet(busRouteSet);
                stations.add(busStation);
                status.getProcessed().add("Station: "+stationCode+", RouteCode: "+routeCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            for (String routeCode: distictRouteCodes) {
                BusRoute busRoute = busRouteMap.get(routeCode);
                storedStations.removeAll(stations);
                for (BusStation busStation: storedStations) {
                    busStation.getRouteSet().remove(busRoute);
                }
            }
            busStationRepository.saveAll(stations);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);

        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    @Async
    @Override
    @Transactional
    public void uploadBusFares(Workbook workbook, String sheetName, String name, String activeDays) throws Exception {
        Sheet worksheet = workbook.getSheet(sheetName);
        List<BusFareVer> busFaresList = new ArrayList<>();
        Row prevRow = null;

        UploadDataStatus status = new UploadDataStatus("Bus fare data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);

        BusFareType busFareType = null;
        try{
            busFareType = busFareTypeRepository.findByBusFareName(name);
        } catch (Exception ex){
            log.error("Failed to fetch bus fare bu name, Exception:{}",ex.getMessage());
        }
        if(busFareType==null){
            busFareType=new BusFareType();
            busFareType.setBusFareName(name);
            busFareType.setBusFareTypeId(CommonUtils.generateRandomString(30));
        }
        busFareType.setActiveDays(activeDays);
        List<BusFareVer> storedBusFares = busFareType.getFares();
        busFareType.setCurrentStatus(CREATED);
        busFareType.setActive(false);
        if (storedBusFares != null) {
            for (BusFareVer busFare : storedBusFares) {
                busFareMap.put(busFare.getFromBusStation().getStationCode() + "-" + busFare.getToBusStation().getStationCode()+"-"+busFare.getBusRoute().getRouteCode(), busFare);
            }
        }
        List<BusRoute> storedRuotes = busRouteRepository.findAll();
        for (BusRoute busRoute : storedRuotes) {
            String vehicle = busRoute.getVehicleNumber() != null ? busRoute.getVehicleNumber() : "";
            busRouteMap.put(busRoute.getRouteNameUp() + "-" + vehicle, busRoute);
            busRouteMap.put(busRoute.getRouteNameDown() + "-" + vehicle, busRoute);
        }
        List<BusStation> storedStations = busStationRepository.findAll();
        for (BusStation busStation : storedStations) {
            busStationMap.put(busStation.getStationCode(), busStation);
        }
        try{
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
                Row row = worksheet.getRow(index);
                if (row == null) {
                    status.getErrors().add("Failed to parse data at line"+index);
                    setUploadStatusRedis(status);
                    throw new Exception("Error While Parcing data at line:" + index);
                }

                String sourceStation = row.getCell(2) != null ? row.getCell(2).toString() : "";
                if (row.getLastCellNum() > 0 && (sourceStation.equals("") || sourceStation.equalsIgnoreCase("stages"))) {
                    prevRow = row;
                    continue;
                }
                if (row.getLastCellNum() > 0 && prevRow != null) {
                    String vehicle = row.getCell(0)!=null?row.getCell(0).toString().trim():"";
                    String routeCode = row.getCell(1).toString().trim() + "-" + vehicle;
                    for (int i = 3; i < row.getLastCellNum(); i++) {
                        if (prevRow.getCell(i) == null || (prevRow.getCell(i) != null &&
                                prevRow.getCell(i).toString().equalsIgnoreCase(""))) {
                            continue;
                        }
                        BusStation fromBusStation = getBusStation(sourceStation);
                        BusStation toBusStation = getBusStation(checkNullEmpty(prevRow.getCell(i).toString()));
                        BusRoute busRoute = getBusRoute(routeCode);
                        if(busRoute==null)
                        {
                            status.getErrors().add(routeCode);
                            setUploadStatusRedis(status);
                            log.error("Bus Route not found with code: {}", routeCode);
                            continue;
                        }
                        BusFareVer busFare = null;
                        String inputFareKey;
                        try{
                            inputFareKey = fromBusStation.getStationCode()+"-"+toBusStation.getStationCode()+"-"+busRoute.getRouteCode();
                        } catch (Exception ex){
                            status.getErrors().add("Exception:" + ex.getMessage()+" for Route:"+routeCode);
                            setUploadStatusRedis(status);
                            log.error("Exception:{}", ex.getMessage());
                            continue;
                        }
                        if (busFareMap.get(inputFareKey) != null) {
                            busFare = busFareMap.get(inputFareKey);
                            checkNullEmpty(row.getCell(i).toString());
                            busFare.setFare(row.getCell(i).getNumericCellValue());
                        } else {
                            busFare = new BusFareVer();
                            checkNullEmpty(row.getCell(i).toString());
                            busFare.setFareId(CommonUtils.generateRandomString(30));
                            busFare.setFare(row.getCell(i).getNumericCellValue());
                            busFare.setBusRoute(busRoute);
                            busFare.setFromBusStation(fromBusStation);
                            busFare.setToBusStation(toBusStation);
                            busFare.setBusFareType(busFareType);
                        }
                        busFareMap.put(inputFareKey, busFare);
                        busFaresList.add(busFare);
                        status.getProcessed().add(routeCode);
                        setUploadStatusRedis(status);
                    }
                }
            }
            busFareType.setFares(busFaresList);
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            busFareTypeRepository.save(busFareType);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);
        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    @Async
    @Override
    @Transactional
    public void uploadBusTimetable(Workbook workbook, String sheetName, String name, String activeDays) throws Exception {
        List<BusTimeTableVer> busTimeTables = new ArrayList<>();
        Sheet workbookSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = workbookSheet.iterator();
        int srNum=0;
        String prevRouteType=null;
        UploadDataStatus status = new UploadDataStatus("Bus timetable data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        BusTimeTableType busTimeTableType = null;
        try{
            busTimeTableType = busTimetableTypeRepository.findByTimeTableName(name);
        } catch (Exception ex){
            log.error("Failed to fetch bus fare bu name, Exception:{}",ex.getMessage());
        }
        if(busTimeTableType==null){
            busTimeTableType=new BusTimeTableType();
            busTimeTableType.setTimeTableName(name);
            busTimeTableType.setBusTimetableId(CommonUtils.generateRandomString(30));
        }
        busTimeTableType.setActiveDays(activeDays);
        busTimeTableType.setCurrentStatus(CREATED);
        busTimeTableType.setActive(false);
        List<BusTimeTableVer> storedBusTimetables = busTimeTableType.getTimeTables();
        if (storedBusTimetables != null) {
            for (BusTimeTableVer busTimeTable : storedBusTimetables) {
                busTimeTableMap.put(busTimeTable.getTripNumber()+"-"+busTimeTable.getRouteType()+"-"+busTimeTable.getBusRoute().getRouteCode()+"-"+busTimeTable.getBusStation().getStationCode(), busTimeTable);
            }
        }

        List<BusRoute> storedRuotes = busRouteRepository.findAll();
        for (BusRoute busRoute : storedRuotes) {
            String vehicle = busRoute.getVehicleNumber() != null ? busRoute.getVehicleNumber() : "";
            busRouteMap.put(busRoute.getRouteNameUp() + "-" + vehicle, busRoute);
            busRouteMap.put(busRoute.getRouteNameDown() + "-" + vehicle, busRoute);
        }

        List<BusStation> storedStations = busStationRepository.findAll();
        for (BusStation busStation : storedStations) {
            busStationMap.put(busStation.getStationCode(), busStation);
        }
        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                String vehicle = nextRow.getCell(1)!=null?nextRow.getCell(1).toString().trim():"";
                String routeCode = checkNullEmpty(nextRow.getCell(0).toString().trim()) + "-" + vehicle;
                BusRoute busRoute = getBusRoute(routeCode);
                if(busRoute==null)
                {
                    status.getErrors().add(routeCode);
                    setUploadStatusRedis(status);
                    log.error("Bus Route not found with code: {}", routeCode);
                    continue;
                }
                BusStation busStation = getBusStation(checkNullEmpty(nextRow.getCell(3).toString().trim()));
                String routeType = checkNullEmpty(nextRow.getCell(2).toString());
                if (prevRouteType!=null && prevRouteType.equalsIgnoreCase(routeType)){
                    srNum++;
                } else {
                    prevRouteType=routeType;
                    srNum=0;
                }
                int lastCellNum = nextRow.getLastCellNum();
                int tripNumber = 0;
                for (int i = 4; i < lastCellNum; i++) {
                    if (nextRow.getCell(i) == null || nextRow.getCell(i).toString().isEmpty()) {
                        continue;
                    }
                    String tripTime = checkNullEmpty(nextRow.getCell(i).getLocalDateTimeCellValue().toLocalTime().toString());
                    if (tripTime.length() < 8) {
                        tripTime = tripTime + ":00";
                    }
                    BusTimeTableVer busTimeTable = null;
                    tripNumber = tripNumber + 1;
                    String inputTimetableKey;
                    try{
                        inputTimetableKey= tripNumber+"-"+routeType+"-"+busRoute.getRouteCode()+"-"+busStation.getStationCode();
                    } catch (Exception ex){
                        status.getErrors().add("Exception:" + ex.getMessage()+" for Route:"+routeCode);
                        setUploadStatusRedis(status);
                        log.error("Failed to add timetable:{}", ex.getMessage());
                        continue;
                    }
                    if (busTimeTableMap.get(inputTimetableKey) != null) {
                        busTimeTable = busTimeTableMap.get(inputTimetableKey);
                        busTimeTable.setDepartureTime(Time.valueOf(tripTime));
                        busTimeTable.setArivalTime(Time.valueOf(tripTime));
                    } else {
                        busTimeTable = new BusTimeTableVer();
                        busTimeTable.setTimeTableId(CommonUtils.generateRandomString(30));
                        busTimeTable.setRouteType(routeType);
                        busTimeTable.setBusStation(busStation);
                        busTimeTable.setBusRoute(busRoute);
                        busTimeTable.setArivalTime(Time.valueOf(tripTime));
                        busTimeTable.setDepartureTime(Time.valueOf(tripTime));
                        busTimeTable.setSrNum(srNum);
                        busTimeTable.setTripNumber(tripNumber);
                        busTimeTable.setTimeTableType(busTimeTableType);
                    }
                    busTimeTableMap.put(inputTimetableKey, busTimeTable);
                    busTimeTables.add(busTimeTable);
                    status.getProcessed().add(routeCode);
                    setUploadStatusRedis(status);
                }
            }
            workbook.close();
            busTimeTableType.setTimeTables(busTimeTables);
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            busTimetableTypeRepository.save(busTimeTableType);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);

        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    private BusStation getBusStation(String stationCode) {
        stationCode = stationCode.trim();
        BusStation busStation = busStationMap.get(stationCode);
        if (busStation != null) return busStation;
        busStation = busStationRepository.findByStationCode(stationCode);
        busStationMap.put(stationCode, busStation);
        return busStation;
    }

    private BusRoute getBusRoute(String routeCode) {
        routeCode = routeCode.trim();
        return busRouteMap.get(routeCode);
    }

    private void setUploadStatusRedis(UploadDataStatus uploadDataStatus){
        try{
            String data = CommonUtils.convertObjectToJsonString(uploadDataStatus);
            redisClient.setValue(redisKey,data, 24*60*60);
        } catch (Exception ex){
            log.error("Failed to save current status, Exception: {}",ex.getMessage());
        }
    }

    @Override
    public UploadDataStatus getUploadStatus(){
        try{

            String data = redisClient.getValue(redisKey);
            return CommonUtils.convertJsonStringToObject(data, UploadDataStatus.class);
        } catch (Exception ex){
            log.error("Failed to save current status, Exception: {}",ex.getMessage());
        }
        return null;
    }

    @Async
    @Override
    @Transactional
    public void enableBusTimetable(String timetableTypeId) throws Exception{
        UploadDataStatus status = new UploadDataStatus("Bus Timetable enable", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        BusTimeTableType timeTableType = busTimetableTypeRepository.findByBusTimetableIdAndCurrentStatus(timetableTypeId, PUBLISHED);
        if(timeTableType==null) {
            status.getErrors().add("Timetable Not in published state");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }

        List<BusTimeTableVer> busTimeTables = timeTableType.getTimeTables();
        List<BusTimeTable> busTimeTableList = new ArrayList<>();
        for(BusTimeTableVer busTimeTableVer: busTimeTables){
            BusTimeTable trip = modelMapper.map(busTimeTableVer, BusTimeTable.class);
            busTimeTableList.add(trip);
        }
        if(busTimeTables.isEmpty()) {
            status.getErrors().add("Failed to enable bus time table");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        try{
            List<BusTimeTableType> timeTableTypes = busTimetableTypeRepository.findAllByIsActive(true);
            timeTableTypes.forEach((tt)->tt.setActive(false));
            busTimetableTypeRepository.saveAll(timeTableTypes);
        } catch (Exception ex){
            log.error("Failed to Disable existing timetable");
            status.getErrors().add(ex.getMessage());
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        status.setCurrentStatus("DB Insert");
        setUploadStatusRedis(status);
        busTimeTableRepository.deleteAll();
        busTimeTableRepository.saveAll(busTimeTableList);
        timeTableType.setActive(true);
        busTimetableTypeRepository.save(timeTableType);
        status.setCurrentStatus("Completed");
        setUploadStatusRedis(status);
    }

    @Async
    @Override
    @Transactional
    public void enableFare(String fareTypeId) throws Exception {
        UploadDataStatus status = new UploadDataStatus("Bus Fare enable", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        BusFareType busFareType = busFareTypeRepository.findByBusFareTypeIdAndCurrentStatus(fareTypeId, PUBLISHED);
        if(busFareType==null) {
            status.getErrors().add("Bus Fare Not in published state");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        List<BusFareVer> busFareVers = busFareType.getFares();
        List<BusFare> busFares = new ArrayList<>();
        for(BusFareVer busFareVer: busFareVers){
            BusFare trip = modelMapper.map(busFareVer, BusFare.class);
            busFares.add(trip);
        }
        if(busFares.isEmpty()) {
            status.getErrors().add("Failed to enable bus fare");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        try{
            List<BusFareType> busFareTypes = busFareTypeRepository.findAllByIsActive(true);
            busFareTypes.forEach((tt)->tt.setActive(false));
            busFareTypeRepository.saveAll(busFareTypes);
        } catch (Exception ex){
            log.error("Failed to Disable existing timetable");
            status.getErrors().add(ex.getMessage());
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        status.setCurrentStatus("DB Insert");
        setUploadStatusRedis(status);
        busFareRepository.deleteAll();
        busFareRepository.saveAll(busFares);
        busFareType.setActive(true);
        busFareTypeRepository.save(busFareType);
        status.setCurrentStatus("Completed");
        setUploadStatusRedis(status);
    }

    private String checkNullEmpty(String value) throws Exception {
        if (value != null && !value.isEmpty())
            return value;

        throw new Exception("Upload and Verification Failed. Please Try again");
    }
}
