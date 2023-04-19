package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.core.model.DAO.GlobalConfig;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.repository.GlobalConfigRepository;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {
    private String redisKey = "global_config:";
    @Autowired
    GlobalConfigRepository globalConfigRepository;
    @Autowired
    RedisClient redisClient;

    @Override
    @Transactional
    public List<GlobalConfigDTO> getGlobalConfig() throws Exception {
        List<GlobalConfig> configs = globalConfigRepository.findAllByIsActive(true);
        List<GlobalConfigDTO> configDTOList = new ArrayList<>();
        for(GlobalConfig config:configs){
            try{
                JsonNode jsonNode = config.getJsonValue();
                if(jsonNode.isObject())
                    configDTOList.add(new  GlobalConfigDTO(config.getKey(), null, config.getJsonValue(), true));
                else
                    configDTOList.add(new  GlobalConfigDTO(config.getKey(), config.getValue(), null, false));
            } catch (Exception ex){
                log.info("Config for Key: {}, is not json", config.getKey());
                configDTOList.add(new  GlobalConfigDTO(config.getKey(), config.getValue(), null, false));
            }
        }
        return configDTOList;
    }

    @Override
    public GlobalConfigDTO getGlobalConfig(String key, boolean isJson) {
        JsonNode jsonValue = null;
        String value = null;
        try{
            if (isJson){
                jsonValue = getConfigJsonValue(key);
            } else {
                value = getConfigValue(key);
            }
            return new GlobalConfigDTO(key, value, jsonValue, isJson);
        } catch (Exception ex){
            log.error("Failed to fetch config, Ex:{}",ex.getMessage());
            return null;
        }
    }

    @Override
    public void setGlobalConfig(GlobalConfigDTO config) throws Exception {
        if (config.isJson()){
            setConfig(config.getKey(), config.getJsonValue());
        } else {
            setConfig(config.getKey(), config.getValue());
        }
    }

    private String getConfigValue(String key) {
        GlobalConfig config = getConfig(key);
        return config.getValue();
    }

    private JsonNode getConfigJsonValue(String key) throws Exception {
        GlobalConfig config = getConfig(key);
        try{
            return config.getJsonValue();
        } catch (Exception ex){
            log.info("Failed to convert to jsonNode: {}", ex.getMessage());
            throw new Exception("Failed to parse provided value");
        }
    }

    private void setConfig(String key, String value) {
        GlobalConfig config = getConfig(key);
        if(config==null){
            config = new GlobalConfig();
            config.setKey(key);
        }
        config.setValue(value);
        saveConfig(config);
    }

    private void setConfig(String key, JsonNode value) throws Exception {
        GlobalConfig config = getConfig(key);
        if(config==null){
            config = new GlobalConfig();
            config.setKey(key);
        }
        try {
            config.setValue(value);
        } catch (Exception ex){
            log.error("Invalid Json Format: {}", ex.getMessage());
            throw new Exception("Invalid Json format Provided");
        }
        saveConfig(config);
    }

    @Transactional
    private GlobalConfig getConfig(String key){
        String redKey = redisKey+key;
        try{
            String keyData = redisClient.getValue(redKey);
            if (keyData!=null){
                return CommonUtils.convertJsonStringToObject(keyData,GlobalConfig.class);
            }
        } catch (Exception ex){
            log.info("Failed to fetch redis key:{}, Exception:{}", key, ex.getMessage());
        }
        GlobalConfig globalConfig = globalConfigRepository.findByKeyAndIsActive(key, true);
        if (globalConfig!=null){
            try{
                redisClient.setValue(redKey, CommonUtils.convertObjectToJsonString(globalConfig));
            } catch (Exception ex){
                log.info("Failed to set redis key:{}, Exception:{}", key, ex.getMessage());
            }
        }
        return globalConfig;
    }

    @Transactional
    private void saveConfig(GlobalConfig config){
        String redKey = redisKey+config.getKey();
        GlobalConfig globalConfig = globalConfigRepository.save(config);
        try{
            redisClient.setValue(redKey, CommonUtils.convertObjectToJsonString(globalConfig));
        } catch (Exception ex){
            log.info("Failed to set redis key:{}, Exception:{}", redKey, ex.getMessage());
        }
    }
}
