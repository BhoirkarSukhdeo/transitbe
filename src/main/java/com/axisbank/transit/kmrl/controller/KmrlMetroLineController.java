package com.axisbank.transit.kmrl.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.kmrl.model.DTO.MetroLineDTO;
import com.axisbank.transit.kmrl.service.MetroLineService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.KMRL_BASE;
import static com.axisbank.transit.kmrl.constant.KmrlApiConstants.METRO_LINE;

@Slf4j
@RestController
@RequestMapping(BASE_URI + KMRL_BASE + METRO_LINE)
public class KmrlMetroLineController {
    @Autowired
    MetroLineService metroLineService;

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> saveStations(@RequestBody MetroLineDTO metroLineDTO) {
        metroLineService.saveLine(metroLineDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "Saved Successfully");
    }
}
