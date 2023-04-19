package com.axisbank.transit.bus.controller;

import com.axisbank.transit.bus.model.DTO.BusStationDTO;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTO;
import com.axisbank.transit.bus.service.BusStationService;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static com.axisbank.transit.bus.constants.BusApiConstants.BUS_BASE;
import static com.axisbank.transit.bus.constants.BusApiConstants.STATION;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.NEAR_BY_STATIONS;

@RestController
@RequestMapping(BASE_URI+BUS_BASE+STATION)
public class BusStationController {

    @Autowired
    BusStationService busStationService;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<BusStationDTO>>> getAllStations(@RequestParam(value = "sourceId",
            required = false, defaultValue = "") String sourceId,@RequestParam(value = "busType",
            required = false, defaultValue = "normal") String busType){
        return BaseResponseType.successfulResponse(busStationService.getStations(sourceId, busType));
    }

    @GetMapping(NEAR_BY_STATIONS)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<NearByBusStationsDTO>>> getNearByStations(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "radius", defaultValue = "2000") long radius){
        List<Double> sourceCoordinates = Arrays.stream(source.split(","))
                .map(Double::new)
                .collect(Collectors.toList());
        return BaseResponseType.successfulResponse(busStationService.getNearbyStations(sourceCoordinates.get(0),
                sourceCoordinates.get(1), radius));
    }
}
