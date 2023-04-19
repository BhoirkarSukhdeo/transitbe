package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.model.DAO.MetroTrip;
import com.axisbank.transit.kmrl.model.DTO.MetroRouteDetailsDTO;
import com.axisbank.transit.kmrl.service.KmrlDataUploadService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.KMRL_BASE;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.TIMETABLE;

@Slf4j
@RestController
@RequestMapping(BASE_URI + KMRL_BASE + TIMETABLE)
public class KmrlTimeTableController {
    @Autowired
    TimeTableService timeTableService;

    @Autowired
    KmrlDataUploadService kmrlDataUploadService;

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER})
    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<Map<String, List<MetroTrip>>>> parseXML(
            @RequestParam("file") MultipartFile file, @RequestParam("timetableName") String timetableName, @RequestParam("activeDays") String activeDays) throws Exception {
        log.info(timetableName);
        String contentType = file.getContentType();
        List<String> validTypes = Arrays.asList("text/xml", "application/xml");
        if (!validTypes.contains(contentType)) {
            log.error("Provided FileType:{}", contentType);
            throw new Exception("Invalid File Type");
        }
        byte[] data = file.getBytes();
        String stringData = new String(data, StandardCharsets.UTF_8);
        kmrlDataUploadService.processXmlTimetable(stringData, timetableName, activeDays);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                "Timetable saved successfully");
    }

    @GetMapping("/{sourceStation}/{destinationStation}")
    public ResponseEntity<BaseResponse<List<MetroRouteDetailsDTO>>> getMetroDetail(@PathVariable String sourceStation,
                                                                                   @PathVariable String destinationStation) throws ParseException {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                timeTableService.getMetroDetails(sourceStation,destinationStation));
    }

    @GetMapping("/departures")
    public ResponseEntity<BaseResponse<List<Time>>> getUpcomingDepartures(@RequestParam(value = "source") String source,
                                                                          @RequestParam(value = "destination", defaultValue = "") String destination){
        if (destination==null || destination.equals(""))
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                timeTableService.getUpcomingDepartures(source));
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                timeTableService.getUpcomingTimings(source,destination));

    }
}
