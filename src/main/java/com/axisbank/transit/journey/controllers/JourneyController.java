package com.axisbank.transit.journey.controllers;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.axisbank.transit.journey.model.requests.GeoTagRequest;
import com.axisbank.transit.journey.model.responses.GeoTagResponse;
import com.axisbank.transit.journey.services.GeoTagService;
import com.axisbank.transit.journey.services.JourneyService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.JOURNEY_BASE_URI;
import static com.axisbank.transit.core.shared.utils.CommonUtils.isNullOrEmpty;

@RestController
@RequestMapping(BASE_URI+JOURNEY_BASE_URI)
public class JourneyController {
    @Autowired
    JourneyService journeyService;

    @Autowired
    GeoTagService geoTagService;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<JourneyPlannerRouteDTO>>> getJourney(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "destination") String destination,
            @RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "journeyType", defaultValue = "%") String journeyType) throws Exception {
        if (isNullOrEmpty(startTime)) return  BaseResponseType.successfulResponse(journeyService.getRoutes(source, destination,
                journeyType));
        return  BaseResponseType.successfulResponse(journeyService.getRoutes(source, destination, startTime, journeyType));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<GeoTagResponse>> getPoly(@RequestBody GeoTagRequest geoTagRequest){
        return BaseResponseType.successfulResponse(geoTagService.setPolygonFromGeoJson(geoTagRequest));

    }
}
