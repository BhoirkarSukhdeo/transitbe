package com.axisbank.transit.bus.controller;

import com.axisbank.transit.bus.constants.BusConstants;
import com.axisbank.transit.bus.model.DTO.*;
import com.axisbank.transit.bus.service.BusAdminService;
import com.axisbank.transit.bus.service.BusDataUploadService;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.axisbank.transit.bus.constants.BusApiConstants.BUS_BASE;
import static com.axisbank.transit.bus.constants.BusConstants.BUS_STATUS_CHANGED;
import static com.axisbank.transit.bus.constants.BusConstants.ENABLE_STARTED;
import static com.axisbank.transit.core.shared.constants.ApiConstants.ADMIN_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;

@RestController
@RequestMapping(BASE_URI+ADMIN_URI+BUS_BASE)
public class BusAdminController {

    @Autowired
    BusAdminService busAdminService;

    @Autowired
    BusDataUploadService busDataUploadService;

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/stations")
    public ResponseEntity<BaseResponse<List<BusStationAdminDTO>>> getAllStations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllStations(page, size, searchParam));
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/routes")
    public ResponseEntity<BaseResponse<List<BusRouteAdminDTO>>> getAllRoutes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllRoutes(page, size, searchParam));
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/fares")
    public ResponseEntity<BaseResponse<List<BusFareAdminDTO>>> getAllFares(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllFares(page, size, searchParam));
    }

    @Secured({RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable")
    public ResponseEntity<BaseResponse<List<BusTimetableAdminDTO>>> getAllTimetable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllTimetable(page, size, searchParam));
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/station/update")
    public ResponseEntity<BaseResponse<String>> updateStation(@Valid @RequestBody BusStationDTO busStationDTO) throws Exception{
        busAdminService.updateStation(busStationDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPDATE_STATION_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/fare/update")
    public ResponseEntity<BaseResponse<String>> updateFare(@Valid @RequestBody BusFareUpdateDTO busFareUpdateDTO) throws Exception{
        busAdminService.updateFare(busFareUpdateDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPDATE_FARE_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/timetable/update")
    public ResponseEntity<BaseResponse<String>> updateTimetable(@Valid @RequestBody BusTimeTableUpdateDTO busTimeTableUpdateDTO) throws Exception{
        busAdminService.updateTimetable(busTimeTableUpdateDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, BusConstants.UPDATE_TIMETABLE_SUCCESS_MESSAGE);
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusTimeTableTypesDTO>>> getAllTimetableTypes() throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllBusTimetables());
    }
    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.USER_ROLE})
    @GetMapping("/timetable/details")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusTimetableAdminDTO>>> getAllTimetableByTypes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam,
            @RequestParam(name = "timetableTypeId", defaultValue = "") String timetableTypeId) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getBusTimetableByType(timetableTypeId, page, size, searchParam));
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/timetable/status-update/{timetableTypeId}/{status}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateTimetableStatus(@PathVariable String timetableTypeId,@PathVariable String status) throws Exception{
        busAdminService.updateBusTimeTableStatus(timetableTypeId, status);
        return BaseResponseType.successfulResponse(BUS_STATUS_CHANGED);
    }

    @Secured({RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/timetable/enable/{timetableTypeId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> enableTimetable(@PathVariable String timetableTypeId) throws Exception{
        busDataUploadService.enableBusTimetable(timetableTypeId);
        return BaseResponseType.successfulResponse(ENABLE_STARTED);
    }



    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE, RoleConstants.MAKER, RoleConstants.USER_ROLE})
    @GetMapping("/fare/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusFareTypesDTO>>> getAllFareTypes() throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getAllBusFares());
    }
    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @GetMapping("/fare/details")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusFareAdminDTO>>> getAllFaresByTypes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchParam", defaultValue = "") String searchParam,
            @RequestParam(name = "fareTypeId", defaultValue = "") String fareTypeId) throws Exception{
        return BaseResponseType.successfulResponse(busAdminService.getBusFareByType(fareTypeId, page, size, searchParam));
    }

    @Secured({RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/fare/status-update/{fareTypeId}/{status}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateFareStatus(@PathVariable String fareTypeId,@PathVariable String status) throws Exception{
        busAdminService.updateFareStatus(fareTypeId, status);
        return BaseResponseType.successfulResponse(BUS_STATUS_CHANGED);
    }

    @Secured({RoleConstants.MAKER, RoleConstants.CHECKER, RoleConstants.PUBLISHER, RoleConstants.ADMIN_ROLE})
    @PostMapping("/fare/enable/{fareTypeId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> enableFare(@PathVariable String fareTypeId) throws Exception{
        busDataUploadService.enableFare(fareTypeId);
        return BaseResponseType.successfulResponse(ENABLE_STARTED);
    }
}
