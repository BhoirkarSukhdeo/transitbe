package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.model.DTO.MetroStationDTO;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTO;
import com.axisbank.transit.kmrl.service.StationService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.KMRL_FARE_CHART_URL;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.TICKET_REFRESH_INTERVAL;
import static com.axisbank.transit.kmrl.constant.Constants.FARE_CHART_IMAGE_URL;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.*;

@Slf4j
@RestController
@RequestMapping(BASE_URI + KMRL_BASE + STATION)
public class KmrlStationController {
    @Autowired
    StationService stationService;

    @Autowired
    GlobalConfigService globalConfigService;

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> saveStations(@RequestBody List<MetroStationDTO> metroStationDTOS) {
        stationService.saveStation(metroStationDTOS);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "Saved Successfully");
    }

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<MetroStationDTO>>> getAllStations(@RequestParam(value = "sourceId",
            required = false, defaultValue = "") String sourceId){
        return BaseResponseType.successfulResponse(stationService.getStations(sourceId));
    }

    @GetMapping("/{stationId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<MetroStationDTO>> getStation(@PathVariable String stationId){
        return BaseResponseType.successfulResponse(stationService.getStation(stationId));
    }

    @GetMapping(NEAR_BY_STATIONS)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<NearByStationsDTO>>> getNearByStations(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "radius") long radius){
        List<Double> sourceCoordinates = Arrays.stream(source.split(","))
                .map(Double::new)
                .collect(Collectors.toList());
        if (radius<=0) return BaseResponseType.successfulResponse(stationService.getNearbyStations(sourceCoordinates.get(0),
                sourceCoordinates.get(1)));
        return BaseResponseType.successfulResponse(stationService.getNearbyStations(sourceCoordinates.get(0),
                sourceCoordinates.get(1), radius));
    }

    @GetMapping(FARE_CHART)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<Map<String,String>>> getFareChart(){
        Map<String, String> fareChart = new HashMap<>();
        String fareChartURL = FARE_CHART_IMAGE_URL;
        try{
            GlobalConfigDTO configDTO = globalConfigService.getGlobalConfig(KMRL_FARE_CHART_URL, false);
            if (configDTO!=null){
                log.info("Fetch fare chart url from db");
                fareChartURL = configDTO.getValue();
            }
        } catch (Exception ex){
            log.error("failed to get fare chart URL, Exception: {}",ex.getMessage());
        }

        fareChart.put("chartUrl", fareChartURL);
        return BaseResponseType.successfulResponse(fareChart);
    }
}
