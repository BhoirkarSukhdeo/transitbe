package com.axisbank.transit.transitCardAPI.model.request.ppim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreparePPIMRequest {

    @JsonProperty("Request")
    private PpimRequest request;

    public PpimRequest getRequest() {
        return request;
    }

    public void setRequest(PpimRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "PreparePPIMRequest{" +
                "request=" + request +
                '}';
    }
}
