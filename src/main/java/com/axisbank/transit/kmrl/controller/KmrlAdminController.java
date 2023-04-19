package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.model.DTO.MetroLineAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroStationAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableAdminDTO;
import com.axisbank.transit.kmrl.model.DTO.MetroTimeTableTypesDTO;
import com.axisbank.transit.kmrl.service.KmrlAdminService;
import com.axisbank.transit.kmrl.service.KmrlDataUploadService;
import com.axisbank.transit.kmrl.service.TimeTableService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.ADMIN_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.Constants.*;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.KMRL_BASE;

@RestController
@RequestMapping(BASE_URI+ADMIN_URI+KMRL_BASE)
public class KmrlAdminController {

    @Autowired
    KmrlAdminService kmrlAdminService;
    @Autowired
    TimeTableService timeTableService;
    @Autowired
    KmrlDataUploadService kmrlDataUploadService;

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/stations")
    public ResponseEntity<BaseResponse<List<MetroStationAdminDTO>>> getAllStations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "displayName", defaultValue = "") String displayName) throws Exception{
        return BaseResponseType.successfulResponse(kmrlAdminService.getAllStations(page, size, displayName));
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/routes")
    public ResponseEntity<BaseResponse<List<MetroLineAdminDTO>>> getAllRoutes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(kmrlAdminService.getAllRoutes(page, size, searchParam));
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable")
    public ResponseEntity<BaseResponse<List<MetroTimeTableAdminDTO>>> getAllTimetable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(kmrlAdminService.getAllTimetable(page, size, searchParam));
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/station/update")
    public ResponseEntity<BaseResponse<String>> updateStation(@Valid @RequestBody MetroStationAdminDTO metroStationAdminDTO) throws Exception{
        kmrlAdminService.updateStation(metroStationAdminDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, METRO_STATION_UPDATE_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/route/update")
    public ResponseEntity<BaseResponse<String>> updateRoute(@Valid @RequestBody MetroLineAdminDTO metroLineAdminDTO) throws Exception{
        kmrlAdminService.updateRoute(metroLineAdminDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, METRO_ROUTE_UPDATED);
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<MetroTimeTableTypesDTO>>> getAllTimetableTypes() throws Exception{
        return BaseResponseType.successfulResponse(timeTableService.getAllMetroTimetables());
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable/details")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<MetroTimeTableAdminDTO>>> getAllTimetableByTypes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam,
            @RequestParam(name = "timetableTypeId", defaultValue = "") String timetableTypeId) throws Exception{
        return BaseResponseType.successfulResponse(timeTableService.getMetroTimetableByType(timetableTypeId, page, size, searchParam));
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/timetable/status-update/{timetableTypeId}/{status}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<MetroTimeTableAdminDTO>>> updateTimetableStatus(@PathVariable String timetableTypeId,@PathVariable String status) throws Exception{
        timeTableService.updateTimeTableStatus(timetableTypeId, status);
        return BaseResponseType.successfulResponse(METRO_TIMETABLE_STATUS_UPDATE);
    }

    @Secured({RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/timetable/enable/{timetableTypeId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<MetroTimeTableAdminDTO>>> enableTimetable(@PathVariable String timetableTypeId) throws Exception{
        kmrlDataUploadService.enableTimetable(timetableTypeId);
        return BaseResponseType.successfulResponse(METRO_TIMETABLE_ENABLED);
    }

}
