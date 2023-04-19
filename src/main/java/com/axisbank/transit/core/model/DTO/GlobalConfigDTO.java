package com.axisbank.transit.core.model.DTO;

import com.fasterxml.jackson.databind.JsonNode;

public class GlobalConfigDTO {
    private String key;
    private String value;
    private JsonNode jsonValue;
    private boolean isJson;

    public GlobalConfigDTO(String key, String value, JsonNode jsonValue, boolean isJson) {
        this.key = key;
        this.value = value;
        this.jsonValue = jsonValue;
        this.isJson = isJson;
    }

    public GlobalConfigDTO() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JsonNode getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(JsonNode jsonValue) {
        this.jsonValue = jsonValue;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }
}
