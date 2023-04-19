package com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupToPrepaid {

    @JsonProperty("RequestBody")
    private RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "TopupToPrepaid{" +
                "requestBody=" + requestBody +
                '}';
    }
}
