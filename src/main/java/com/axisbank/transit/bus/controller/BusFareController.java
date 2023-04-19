package com.axisbank.transit.bus.controller;

import com.axisbank.transit.bus.model.DTO.BusFareChartDTO;
import com.axisbank.transit.bus.model.DTO.BusStationDTO;
import com.axisbank.transit.bus.service.BusFareService;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.axisbank.transit.bus.constants.BusApiConstants.*;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;

@RestController
@RequestMapping(BASE_URI+BUS_BASE+FARE)
public class BusFareController {

    @Autowired
    private BusFareService busFareService;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusFareChartDTO>>> getAllRouteFares(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                                @RequestParam(name = "size", defaultValue = "10") int size) throws Exception {
        return BaseResponseType.successfulResponse(busFareService.getAllRouteFares(page, size));
    }
}
