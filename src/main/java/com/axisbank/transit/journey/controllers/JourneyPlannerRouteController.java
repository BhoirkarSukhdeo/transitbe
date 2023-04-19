package com.axisbank.transit.journey.controllers;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.journey.constants.JourneyConstants;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerConfirmedRouteDTO;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.axisbank.transit.journey.services.JourneyPlannerRouteService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.JOURNEY_PLANNER_ROUTE;

@RestController
@RequestMapping(BASE_URI+JOURNEY_PLANNER_ROUTE)
public class JourneyPlannerRouteController {

    @Autowired
    JourneyPlannerRouteService journeyPlannerRouteService;

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> saveJourneyPlannerRoute(@RequestBody JourneyPlannerRouteDTO journeyPlannerRouteDTO) throws Exception {
        journeyPlannerRouteService.saveJourneyPlannerRoute(journeyPlannerRouteDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, JourneyConstants.SAVE_JOURNEY_PLANNER_ROUTE_SUCCESS_MESSAGE);
    }

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<JourneyPlannerConfirmedRouteDTO>> getJourneyPlannerRoute() throws Exception {
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, journeyPlannerRouteService.getJourneyPlannerRoute());
    }

    @GetMapping("/end-journey")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> endJourney() throws Exception {
        journeyPlannerRouteService.endJourney();
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, JourneyConstants.JOURNEY_END_SUCCESS_MESSAGE);
    }
}
