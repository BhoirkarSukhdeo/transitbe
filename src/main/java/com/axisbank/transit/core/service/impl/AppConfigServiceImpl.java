package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.core.model.DTO.AppBaseConfigDTO;
import com.axisbank.transit.core.model.DTO.AppVersionDTO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.AppConfigService;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.APP_MIN_VERSION;

@Slf4j
@Service
public class AppConfigServiceImpl implements AppConfigService {
    @Autowired
    GlobalConfigService globalConfigService;
    @Override
    public AppBaseConfigDTO getAppConfig() {
        GlobalConfigDTO appConfig = globalConfigService.getGlobalConfig(APP_MIN_VERSION, true);
        AppBaseConfigDTO appBaseConfigDTO = new AppBaseConfigDTO();
        AppVersionDTO appVersionDTO = new AppVersionDTO();
        String androidVer = "1.0";
        String iosVer = "1.0";
        if (appConfig!=null){
            JsonNode appData = appConfig.getJsonValue();
            androidVer = appData.get("android").textValue();
            iosVer = appData.get("ios").textValue();
        }
        appVersionDTO.setAndroidMinVersion(androidVer);
        appVersionDTO.setIosMinVersion(iosVer);
        appBaseConfigDTO.setAppVersion(appVersionDTO);
        return appBaseConfigDTO;
    }
}
