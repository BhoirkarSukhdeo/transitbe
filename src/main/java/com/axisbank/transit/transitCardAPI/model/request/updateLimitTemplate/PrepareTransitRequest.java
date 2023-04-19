package com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrepareTransitRequest {

    @JsonProperty("request")
    public Request request;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "PrepareSoapRequest{" +
                "request=" + request +
                '}';
    }
}
