package com.axisbank.transit.core.service;

import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface GlobalConfigService {

    List<GlobalConfigDTO> getGlobalConfig() throws Exception;
    GlobalConfigDTO getGlobalConfig(String key, boolean isJson);
    void setGlobalConfig(GlobalConfigDTO config) throws Exception;
}
