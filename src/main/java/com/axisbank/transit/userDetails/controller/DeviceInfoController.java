package com.axisbank.transit.userDetails.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.userDetails.constants.UserDetailsConstants;
import com.axisbank.transit.userDetails.model.DTO.DeviceInfoDTO;
import com.axisbank.transit.userDetails.service.DeviceInfoService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.DEVICE_INFO_URI;

@RestController
@RequestMapping(BASE_URI+DEVICE_INFO_URI)
public class DeviceInfoController {

    @Autowired
    DeviceInfoService deviceInfoService;

    @PostMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> updateDeviceInfo(@RequestBody DeviceInfoDTO deviceInfoDTO) throws Exception {

        deviceInfoService.updateDeviceInfo(deviceInfoDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, UserDetailsConstants.UPDATE_DEVICE_INFO_SUCCESS_MESSAGE);
    }

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<DeviceInfoDTO>> getDeviceInfo() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, deviceInfoService.getDeviceInfo());
    }
}
