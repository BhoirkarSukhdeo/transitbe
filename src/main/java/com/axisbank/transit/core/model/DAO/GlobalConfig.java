package com.axisbank.transit.core.model.DAO;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "global_config")
@Audited
public class GlobalConfig extends BaseEntity {
    @Column(name = "key", unique = true)
    private String key;

    @Lob
    @Column(name = "value")
    private String value;

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
    @JsonIgnore
    public JsonNode getJsonValue() throws JsonProcessingException {
        return CommonUtils.convertJsonStringToObject(value, JsonNode.class);
    }

    public void setValue(JsonNode value) throws JsonProcessingException {
        this.value = CommonUtils.convertObjectToJsonString(value);
    }
}
