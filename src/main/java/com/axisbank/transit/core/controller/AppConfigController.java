package com.axisbank.transit.core.controller;

import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.service.AppConfigService;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.axisbank.transit.core.shared.constants.ApiConstants.APP_CONFIG;
import static com.axisbank.transit.core.shared.constants.ApiConstants.BASE_URI;

@Slf4j
@RestController
@RequestMapping(BASE_URI+APP_CONFIG)

public class AppConfigController {
    @Autowired
    private AppConfigService appConfigService;
    @RequestMapping( value = "", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<GlobalConfigDTO>> getAppConfig()
            throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,
                appConfigService.getAppConfig());
    }
}
