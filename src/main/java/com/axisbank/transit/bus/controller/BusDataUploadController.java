package com.axisbank.transit.bus.controller;

import com.axisbank.transit.bus.constants.BusConstants;
import com.axisbank.transit.bus.service.BusDataUploadService;
import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BUS_DATA_UPLOAD_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI+BUS_DATA_UPLOAD_URI)
public class BusDataUploadController {

    @Autowired
    BusDataUploadService busDataUploadService;

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/station")
    public ResponseEntity<String> uploadStations(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "bus_station", null, null);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/bus-route")
    public ResponseEntity<String> uploadBusRoutes(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "bus_routes", null, null);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/bus-Station-route")
    public ResponseEntity<String> uploadBusStationRoutes(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "bus_station_routes", null, null);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER})
    @PostMapping("/bus-fare")
    public ResponseEntity<String> uploadBusFares(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("sheetName") String sheetName,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("activeDays") String activeDays) throws Exception {
        processSheet(file, sheetName, "bus_fares", name, activeDays);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER})
    @PostMapping("/bus-timetable")
    public ResponseEntity<String> uploadBusTimetable(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("sheetName") String sheetName,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("activeDays") String activeDays) throws Exception {
        processSheet(file, sheetName, "bus_timetable", name, activeDays);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER})
    @GetMapping("/status")
    public ResponseEntity<UploadDataStatus> uploadDataStatus(){
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, busDataUploadService.getUploadStatus());
    }


    private void processSheet(MultipartFile file, String sheetName, String function, String name, String activeDays) throws Exception {
        String contentType = file.getContentType();
        List<String> validTypes = Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/octet-stream");
        if (!validTypes.contains(contentType)) {
            log.error("Provided FileType:{}", contentType);
            throw new Exception("Invalid File Type");
        }
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        switch(function){
            case "bus_station" :
                busDataUploadService.uploadStations(workbook, sheetName);
                break;
            case "bus_routes" :
                busDataUploadService.uploadBusRoutes(workbook, sheetName);
                break;
            case "bus_station_routes" :
                busDataUploadService.uploadBusStationRoutes(workbook, sheetName);
                break;
            case "bus_fares" :
                busDataUploadService.uploadBusFares(workbook, sheetName, name, activeDays);
                break;
            case "bus_timetable" :
                busDataUploadService.uploadBusTimetable(workbook, sheetName, name, activeDays);
                break;
            default:
                throw new Exception("Invalid upload type");
        }
    }

}
