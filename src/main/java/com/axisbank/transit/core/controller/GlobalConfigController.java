package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;
import static com.axisbank.transit.core.shared.constants.ApiConstants.GLOBAL_CONFIG;

@Slf4j
@RestController
@RequestMapping(BASE_URI+GLOBAL_CONFIG)
public class GlobalConfigController {

    @Autowired
    private GlobalConfigService globalConfigService;

    @RequestMapping( value = "/{key}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<GlobalConfigDTO>> getGlobalConfig(@PathVariable String key,
                                                                         @RequestParam(value = "isJson",
                                                                                 defaultValue = "false") boolean isJson)
            throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                globalConfigService.getGlobalConfig(key, isJson));
    }
    @RequestMapping( value = "", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<GlobalConfigDTO>>> getAllGlobalConfig()
            throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                globalConfigService.getGlobalConfig());
    }

    @Secured(RoleConstants.ADMIN_ROLE)
    @RequestMapping( value = "", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<GlobalConfigDTO>> setGlobalConfig(@RequestBody GlobalConfigDTO globalConfigDTO) throws Exception{
        globalConfigService.setGlobalConfig(globalConfigDTO);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "Global config saved successfully");
    }
}
