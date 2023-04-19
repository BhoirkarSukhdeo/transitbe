package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.core.model.DTO.UploadDataStatus;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.service.KmrlDataUploadService;
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
import static com.axisbank.transit.kmrl.constant.Constants.METRO_UPLOAD_SUCCESS_MESSAGE;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.KMRL_BASE;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.UPLOAD_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI + KMRL_BASE + UPLOAD_URI)
public class KmrlDataUploadController {

    @Autowired
    KmrlDataUploadService kmrlDataUploadService;

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/station")
    public ResponseEntity<String> uploadStations(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "metro_station");
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, METRO_UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/route")
    public ResponseEntity<String> uploadRoutes(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "metro_routes");
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, METRO_UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/station-route")
    public ResponseEntity<String> uploadStationRoutes(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("sheetName") String sheetName) throws Exception {
        processSheet(file, sheetName, "metro_station_routes");
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, METRO_UPLOAD_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER})
    @GetMapping("/status")
    public ResponseEntity<UploadDataStatus> uploadDataStatus(){
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, kmrlDataUploadService.getUploadStatus());
    }

    private void processSheet(MultipartFile file, String sheetName, String function) throws Exception {
        String contentType = file.getContentType();
        List<String> validTypes = Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/octet-stream");
        if (!validTypes.contains(contentType)) {
            log.error("Provided FileType:{}", contentType);
            throw new Exception("Invalid File Type");
        }
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        switch(function){
            case "metro_station" :
                kmrlDataUploadService.uploadStations(workbook, sheetName);
                break;
            case "metro_routes" :
                kmrlDataUploadService.uploadRoutes(workbook, sheetName);
                break;
            case "metro_station_routes" :
                kmrlDataUploadService.uploadStationRoutes(workbook, sheetName);
                break;
            default:
                throw new Exception("Invalid upload type");
        }
    }
}
