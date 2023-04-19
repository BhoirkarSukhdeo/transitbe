package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.model.response.DashboardResponseDTO;
import com.axisbank.transit.core.service.DashboardService;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.userDetails.model.DTO.UserDetailsDTO;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.DASHBOARD_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI+DASHBOARD_URI)
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping( value = "", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<UserDetailsDTO>> getLoggedInUserDetails() throws Exception{
        DashboardResponseDTO dashboardResponse = dashboardService.getDasboardDetails();
        if (dashboardResponse != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, dashboardResponse);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, "Error in getting Dashboard Details");
    }
}
