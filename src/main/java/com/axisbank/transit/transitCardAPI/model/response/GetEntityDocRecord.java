package com.axisbank.transit.transitCardAPI.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetEntityDocRecord {

    @JsonProperty("ORGKEY")
    private String orgkey;

    public String getOrgkey() {
        return orgkey;
    }

    public void setOrgkey(String orgkey) {
        this.orgkey = orgkey;
    }

}