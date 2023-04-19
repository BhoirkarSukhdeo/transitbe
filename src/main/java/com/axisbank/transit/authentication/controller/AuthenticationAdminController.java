package com.axisbank.transit.authentication.controller;

import com.axisbank.transit.authentication.model.DTO.AdminUserDTO;
import com.axisbank.transit.authentication.model.DTO.UserDetailsAdminDTO;
import com.axisbank.transit.authentication.service.AuthAdminService;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.axisbank.transit.authentication.constants.AuthenticationConstants.CREATE_USER_SUCCESS_MESSAGE;
import static com.axisbank.transit.authentication.constants.AuthenticationConstants.UPDATE_USER_SUCCESS_MESSAGE;
import static com.axisbank.transit.core.shared.constants.ApiConstants.ADMIN_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.USERS_URI;

@Slf4j
@RestController
@RequestMapping(ApiConstants.BASE_URI+ADMIN_URI+USERS_URI)
public class AuthenticationAdminController {

    @Autowired
    private AuthAdminService authAdminService;

    @Secured(RoleConstants.ADMIN_ROLE)
    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<AdminUserDTO>>> getAllAdminUsers() {
        log.info("Request receive for fetching all admin portal users");
        List<AdminUserDTO> adminUserDTOS = authAdminService.getAllAdminUsers();
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, adminUserDTOS);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("")
    public ResponseEntity<BaseResponse<String>> createUser(@Valid @RequestBody UserDetailsAdminDTO userDetailsAdminDTO) throws Exception{
        authAdminService.createUser(userDetailsAdminDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, CREATE_USER_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @PostMapping("/update")
    public ResponseEntity<BaseResponse<String>> updateUser(@Valid @RequestBody UserDetailsAdminDTO userDetailsAdminDTO) throws Exception{
        authAdminService.updateUser(userDetailsAdminDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, UPDATE_USER_SUCCESS_MESSAGE);
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @GetMapping("/roles")
    public ResponseEntity<BaseResponse<List<String>>> getAllRoles() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, authAdminService.getAllRoles());
    }
}
