package com.axisbank.transit.kmrl.service.impl;

import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.kmrl.model.DAO.*;
import com.axisbank.transit.kmrl.repository.MetroLineRepository;
import com.axisbank.transit.kmrl.repository.MetroStationRepository;
import com.axisbank.transit.kmrl.repository.MetroTimeTableTypeRepository;
import com.axisbank.transit.kmrl.repository.MetroTripRepository;
import com.axisbank.transit.kmrl.service.KmrlDataUploadService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.utils.CommonUtils.addSecondsToTime;
import static com.axisbank.transit.core.shared.utils.XMLUtils.xmlStringToJson;
import static com.axisbank.transit.kmrl.constant.Constants.KMRL_BLUE_LINE;
import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.CREATED;
import static com.axisbank.transit.kmrl.constant.WorkFlowConstants.PUBLISHED;

@Service
@Slf4j
public class KmrlDataUploadServiceImpl implements KmrlDataUploadService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    MetroStationRepository metroStationRepository;

    @Autowired
    MetroLineRepository metroLineRepository;

    @Autowired
    RedisClient redisClient;

    @Autowired
    TimeTableService timeTableService;

    @Autowired
    MetroTimeTableTypeRepository metroTimeTableTypeRepository;

    @Autowired
    MetroTripRepository metroTripRepository;

    Map<String, MetroStation> metroStationMap = new HashMap<>();
    Map<String, MetroLine> metroRouteMap = new HashMap<>();
    private final String redisKey = "METRO_DATA_UPLOAD";

    @Async
    @Override
    @Transactional
    public void uploadStations(Workbook workbook, String sheetName) throws Exception {
        List<MetroStation> stations = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = sheet.iterator();
        UploadDataStatus status = new UploadDataStatus("Metro station data upload", new HashSet<>(),new HashSet<>(),
                "Started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        List<MetroStation> storedStations = metroStationRepository.findAll();
        for (MetroStation metroStation : storedStations) {
            metroStationMap.put(metroStation.getStationCode(), metroStation);
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
                String stationCode = checkNullEmpty(nextRow.getCell(7).toString().trim());
                MetroStation metroStation = null;
                String stationDisplayName = checkNullEmpty(nextRow.getCell(0).toString());
                String latitude = checkNullEmpty(nextRow.getCell(2).toString());
                String longitude = checkNullEmpty(nextRow.getCell(3).toString());
                String distance = checkNullEmpty(nextRow.getCell(1).toString());
                String stationCodeUp = checkNullEmpty(nextRow.getCell(4).toString());
                String stationCodeDn = checkNullEmpty(nextRow.getCell(5).toString());
                String selStationID = checkNullEmpty(nextRow.getCell(6).toString());
                if (metroStationMap.get(stationCode) != null) {
                    log.debug("Station code already present: " + stationCode);
                    metroStation = metroStationMap.get(stationCode);
                    metroStation.setLatitude(Double.parseDouble(latitude));
                    metroStation.setLongitude(Double.parseDouble(longitude));
                    metroStation.setDisplayName(stationDisplayName);

                } else {
                    metroStation = new MetroStation();
                    metroStation.setStationId(CommonUtils.generateRandomString(30));
                    metroStation.setDisplayName(stationDisplayName);
                    metroStation.setStationCode(stationCode);
                    metroStation.setLatitude(Double.parseDouble(latitude));
                    metroStation.setLongitude(Double.parseDouble(longitude));
                    metroStation.setDistance(Double.parseDouble(distance));
                    metroStation.setStationCodeUp(stationCodeUp);
                    metroStation.setStationCodeDn(stationCodeDn);
                    metroStation.setSetStationId((int)nextRow.getCell(6).getNumericCellValue());
                }
                stations.add(metroStation);
                metroStationMap.put(stationCode, metroStation);
                status.getProcessed().add(stationCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            metroStationRepository.saveAll(stations);
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
    public void uploadRoutes(Workbook workbook, String sheetName) throws Exception {
        List<MetroLine> metroLines = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = sheet.iterator();
        UploadDataStatus status = new UploadDataStatus("Metro routes data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        List<MetroLine> storedMetroLines = metroLineRepository.findAll();
        for (MetroLine metroLine : storedMetroLines) {
            metroRouteMap.put(metroLine.getLineCode(), metroLine);
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
                String lineCode = checkNullEmpty(nextRow.getCell(1).toString().trim());
                String displayName = checkNullEmpty(nextRow.getCell(0).toString());
                MetroLine metroLine = null;
                if (metroRouteMap.get(lineCode) != null) {
                    log.debug("Line code already present: " + lineCode);
                    metroLine = metroRouteMap.get(lineCode);
                    metroLine.setDisplayName(displayName);
                } else {
                    metroLine = new MetroLine();
                    metroLine.setLineId(CommonUtils.generateRandomString(30));
                    metroLine.setDisplayName(displayName);
                    metroLine.setLineCode(lineCode);
                }
                metroLines.add(metroLine);
                metroRouteMap.put(lineCode, metroLine);
                status.getProcessed().add(lineCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            metroLineRepository.saveAll(metroLines);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);
        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add("Upload and Verification Failed. Please Try again");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    private String checkNullEmpty(String value) throws Exception {
        if (value != null && !value.isEmpty())
            return value;

        throw new Exception("Upload and Verification Failed. Please Try again");
    }

    @Override
    @Transactional
    public void uploadStationRoutes(Workbook workbook, String sheetName) throws Exception {
        List<MetroStation> stations = new ArrayList<>();
        Sheet firstSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = firstSheet.iterator();
        UploadDataStatus status = new UploadDataStatus("Metro station routes map data upload", new HashSet<>(),new HashSet<>(),
                "started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);

        List<MetroStation> storedStations = metroStationRepository.findAll();
        for (MetroStation metroStation : storedStations) {
            metroStationMap.put(metroStation.getStationCode(), metroStation);
        }

        List<MetroLine> storedRuotes = metroLineRepository.findAll();
        for (MetroLine metroLine : storedRuotes) {
            metroRouteMap.put(metroLine.getLineCode(), metroLine);
        }

        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            Set<String> distinctMetroLines = new HashSet<>();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }

                String lineCode = checkNullEmpty(nextRow.getCell(1).toString().trim());
                String stationCode = checkNullEmpty(nextRow.getCell(0).toString().trim());
                MetroLine metroLine = null;
                MetroStation metroStation = null;
                if (metroRouteMap.get(lineCode) != null) {
                    distinctMetroLines.add(lineCode);
                    metroLine = metroRouteMap.get(lineCode);
                } else {
                    status.getErrors().add("Route: "+lineCode);
                    setUploadStatusRedis(status);
                    log.debug("Line code does not present in DB"+lineCode);
                    continue;
                }
                if (metroStationMap.get(stationCode) != null) {
                    metroStation = metroStationMap.get(stationCode);
                } else {
                    status.getErrors().add("Station: "+stationCode);
                    setUploadStatusRedis(status);
                    log.debug("Station code does not present in DB"+stationCode);
                    continue;
                }
                Set<MetroLine> metroLineSet = metroStation.getMetroLine();
                metroLineSet.add(metroLine);
                metroStation.setMetroLine(metroLineSet);
                stations.add(metroStation);
                status.getProcessed().add("Station: "+stationCode+", LineCode: "+lineCode);
                setUploadStatusRedis(status);
            }
            workbook.close();
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            for (String metroLineCode: distinctMetroLines) {
                MetroLine metroLine = metroRouteMap.get(metroLineCode);
                storedStations.removeAll(stations);
                for (MetroStation metroStation: storedStations) {
                    metroStation.getMetroLine().remove(metroLine);
                }
            }
            metroStationRepository.saveAll(stations);
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
    public void processXmlTimetable(String xmlString, String timeTableName, String activeDays) throws ParseException {
        JSONObject jsonObject = xmlStringToJson(xmlString);
        JSONArray trips = jsonObject.getJSONObject("ROOT").getJSONObject("SCHEDULE")
                .getJSONObject("TRIPS").getJSONArray("TRIP");
        UploadDataStatus status = new UploadDataStatus("Metro timetable data upload", new HashSet<>(),new HashSet<>(),
                "Started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        List<MetroTripVer> metroTrips = new ArrayList<>();
        MetroTimeTableType timeTableType = new MetroTimeTableType();
        timeTableType.setMtTimetableId(CommonUtils.generateRandomString(30));
        timeTableType.setTimeTableName(timeTableName);
        timeTableType.setActiveDays(activeDays);
        timeTableType.setCurrentStatus(CREATED);
        timeTableType.setActive(false);
        try {
            status.setCurrentStatus("Processing");
            setUploadStatusRedis(status);
            for (int i = 0; i < trips.length(); i++) {
                MetroTripVer trip = new MetroTripVer();

                JSONObject tripDetails = trips.getJSONObject(i);

                String startTime = tripDetails.getString("ENTRY_TIME");

                trip.setMtTripId(CommonUtils.generateRandomString(30));
                trip.setTripId(tripDetails.getString("TRIP_ID"));
                trip.setTripNumber(tripDetails.getNumber("NUMBER").toString());
                try {
                    trip.setPrevNumber(tripDetails.getNumber("PREVIOUS_NUMBER").toString());
                } catch (Exception ex) {
                    trip.setPrevNumber(tripDetails.getString("PREVIOUS_NUMBER"));
                }
                try {
                    trip.setNextNumber(tripDetails.getNumber("NEXT_NUMBER").toString());
                } catch (Exception ex) {
                    trip.setNextNumber(tripDetails.getString("NEXT_NUMBER"));
                }
                trip.setDirection(tripDetails.getString("DIRECTION"));
                try {
                    trip.setServiceId(tripDetails.getNumber("SERVICE_ID").toString());
                } catch (Exception ex) {
                    trip.setServiceId(tripDetails.getString("SERVICE_ID"));
                }
                trip.setTotalDistance(tripDetails.getNumber("DISTANCE").toString());
                trip.setStartTime(Time.valueOf(startTime));
                JSONArray allStationsStops = tripDetails.getJSONArray("STOP");
                JSONArray allStationsRuns = tripDetails.getJSONArray("RUN");

                List<MetroTimeTableVer> allTimeTables = new ArrayList<>();

                MetroTimeTableVer firstMetroTimeTable = new MetroTimeTableVer();
                JSONObject firstStopObject = allStationsStops.getJSONObject(0);

                long totalRuntime = 0;
                int firstDwelltime = firstStopObject.getInt("DWELLTIME");

                firstMetroTimeTable.setArivalTime(Time.valueOf(startTime));
                firstMetroTimeTable.setPrevRuntime(totalRuntime);
                firstMetroTimeTable.setDwelltime(firstDwelltime);
                firstMetroTimeTable.setSrNum(0);
                firstMetroTimeTable.setTimeTableId(CommonUtils.generateRandomString(30));
                totalRuntime = totalRuntime + firstDwelltime;
                firstMetroTimeTable.setDepartureTime(Time.valueOf(addSecondsToTime(startTime, (int) totalRuntime)));
                MetroStation firstMetroStation = timeTableService.getMetroStation(firstStopObject.getString("TOP"));
                if (firstMetroStation != null) {
                    firstMetroTimeTable.setStation(firstMetroStation);
                    firstMetroTimeTable.setStationName(firstMetroStation.getDisplayName());
                }
                firstMetroTimeTable.setMetroLineName(KMRL_BLUE_LINE);
                firstMetroTimeTable.setTrip(trip);
                allTimeTables.add(firstMetroTimeTable);
                for (int j = 0, k = 1; j < allStationsRuns.length(); j++, k++) {
                    MetroTimeTableVer metroTimeTable = new MetroTimeTableVer();
                    JSONObject runObject = allStationsRuns.getJSONObject(j);
                    JSONObject stopObject = allStationsStops.getJSONObject(k);
                    long prevRunTime = runObject.getLong("RUNTIME");
                    totalRuntime = totalRuntime + prevRunTime;
                    int dwelltime = stopObject.getInt("DWELLTIME");
                    String arivalTime = addSecondsToTime(startTime, (int) totalRuntime);
                    metroTimeTable.setArivalTime(Time.valueOf(arivalTime));
                    metroTimeTable.setPrevRuntime(prevRunTime);
                    metroTimeTable.setDwelltime(dwelltime);
                    metroTimeTable.setSrNum(k);
                    metroTimeTable.setTotalRuntime(totalRuntime);
                    metroTimeTable.setTimeTableId(CommonUtils.generateRandomString(30));
                    totalRuntime = totalRuntime + dwelltime;
                    metroTimeTable.setDepartureTime(Time.valueOf(addSecondsToTime(startTime, (int) totalRuntime)));
                    MetroStation metroStation = timeTableService.getMetroStation(stopObject.getString("TOP"));
                    if (metroStation != null) {
                        metroTimeTable.setStation(metroStation);
                        metroTimeTable.setStationName(metroStation.getDisplayName());
                    }
                    metroTimeTable.setMetroLineName(KMRL_BLUE_LINE);
                    metroTimeTable.setTrip(trip);
                    allTimeTables.add(metroTimeTable);
                }
                trip.setTimeTables(allTimeTables);
                trip.setTimeTableType(timeTableType);
                metroTrips.add(trip);
            }
            timeTableType.setTrips(metroTrips);
            status.setCurrentStatus("DB Insert");
            setUploadStatusRedis(status);
            metroTimeTableTypeRepository.save(timeTableType);
            status.setCurrentStatus("Completed");
            setUploadStatusRedis(status);
        } catch (Exception ex){
            log.error("Failed to upload data:{}",ex.getMessage());
            status.getErrors().add(ex.getMessage());
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
    }

    @Async
    @Override
    @Transactional
    public void enableTimetable(String timetableTypeId) throws Exception{
        UploadDataStatus status = new UploadDataStatus("Metro timetable Enable", new HashSet<>(),new HashSet<>(),
                "Started",CommonUtils.getCurrentDateTime("dd/MM/yyyy HH:mm"));
        setUploadStatusRedis(status);
        MetroTimeTableType timeTableType = metroTimeTableTypeRepository.findByMtTimetableIdAndCurrentStatus(timetableTypeId, PUBLISHED);
        if(timeTableType==null) {
            status.getErrors().add("Timetable Not in published state");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }

        List<MetroTripVer> metroTrips = timeTableType.getTrips();
        List<MetroTrip> metroTripList = new ArrayList<>();
        for(MetroTripVer metroTrip: metroTrips){
            MetroTrip trip = modelMapper.map(metroTrip, MetroTrip.class);
            List<MetroTimeTable> timeTables = trip.getTimeTables().stream()
                    .map(element->modelMapper.map(element, MetroTimeTable.class))
                    .collect(Collectors.toList());
            trip.setTimeTables(timeTables);
            metroTripList.add(trip);
        }
        if(metroTripList.isEmpty()) {
            status.getErrors().add("metro trip list is empty");
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        try{
            List<MetroTimeTableType> timeTableTypes = metroTimeTableTypeRepository.findAllByIsActive(true);
            timeTableTypes.forEach((tt)->tt.setActive(false));
            metroTimeTableTypeRepository.saveAll(timeTableTypes);
        } catch (Exception ex){
            log.error("Failed to Disable existing timetable");
            status.getErrors().add(ex.getMessage());
            status.setCurrentStatus("Failed");
            setUploadStatusRedis(status);
        }
        status.setCurrentStatus("DB Insert");
        setUploadStatusRedis(status);
        metroTripRepository.deleteAll();
        metroTripRepository.saveAll(metroTripList);
        timeTableType.setActive(true);
        metroTimeTableTypeRepository.save(timeTableType);
        status.setCurrentStatus("Completed");
        setUploadStatusRedis(status);
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

    private void setUploadStatusRedis(UploadDataStatus uploadDataStatus){
        try{
            String data = CommonUtils.convertObjectToJsonString(uploadDataStatus);
            redisClient.setValue(redisKey,data, 24*60*60);
        } catch (Exception ex){
            log.error("Failed to save current status, Exception: {}",ex.getMessage());
        }
    }
}
