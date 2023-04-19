package com.axisbank.transit.userDetails.controller;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.userDetails.constants.UserDetailsConstants;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;
import com.axisbank.transit.userDetails.model.DTO.UserConfigDTO;
import com.axisbank.transit.userDetails.model.DTO.UserDetailsDTO;
import com.axisbank.transit.userDetails.service.UserInfoService;
import com.axisbank.transit.userDetails.util.UserUtil;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static com.axisbank.transit.core.shared.constants.ApiConstants.USER_CONFIG_URI;
import static com.axisbank.transit.core.shared.constants.UtilsConstants.COUNTRY_CODE_WITHOUT_PLUS;

@RestController
@RequestMapping(ApiConstants.BASE_URI+ ApiConstants.USERS_URI)
public class UserDetailsController {

    @Autowired
    UserInfoService userDetailsService;

    @Autowired
    UserUtil userUtil;

    @Secured(RoleConstants.ADMIN_ROLE)
    @RequestMapping( value = "/{userId}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<UserDetailsDTO>> getUserDetails(@PathVariable String userId) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, userDetailsService.getUserDetails(userId));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateUserDetails(@RequestBody UserDetailsDTO userDetails) throws Exception {
        if(userDetails.getMobile()!=null && !userDetails.getMobile().equals("") && userDetails.getMobile().length() == 10){
            // add 91 prefix for mobile numbers
            userDetails.setMobile(COUNTRY_CODE_WITHOUT_PLUS+userDetails.getMobile());
        }
        AuthenticationDAO updatedUserDetails = userDetailsService.updateUserDetails(userDetails);
        if (updatedUserDetails != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, UserDetailsConstants.UPDATE_USER_SUCCESS_MESSAGE);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, UserDetailsConstants.UPDATE_USER_FAILURE_MESSAGE);
    }

    @RequestMapping(value = "/shared-preference", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateUserConfiguration(@RequestBody UserConfigurationDTO userConfiguration) throws Exception {
        DAOUser updatedUserDetails = userDetailsService.updateUserConfiguration(userConfiguration);
        if (updatedUserDetails != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, UserDetailsConstants.UPDATE_USER_SUCCESS_MESSAGE);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, UserDetailsConstants.UPDATE_USER_FAILURE_MESSAGE);
    }

    @RequestMapping( value = "", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<UserDetailsDTO>> getLoggedInUserDetails() throws Exception{
        UserDetailsDTO userDetails = userDetailsService.getLoggedInUserDetails();
        if (userDetails != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, userDetails);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, UserDetailsConstants.GET_USER_FAILURE_MESSAGE);
    }

    @GetMapping(USER_CONFIG_URI)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<UserConfigDTO>> getUserConfig() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, userDetailsService.getUserConfig());
    }
}
