package com.axisbank.transit.userDetails.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.userDetails.model.DTO.OccupationDTO;
import com.axisbank.transit.userDetails.service.OccupationService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_URI+ ApiConstants.OCCUPATION_URI)
public class OccupationController {

    @Autowired
    OccupationService occupationService;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<OccupationDTO>>> getAllOccupations() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, occupationService.getAllOccupations());
    }
}
