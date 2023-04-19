package com.axisbank.transit.transitCardAPI.model.request.getCardInfoForPortal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCardInfoForPortal {

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
        return "GetCardInfoForPortal{" +
                "requestBody=" + requestBody +
                '}';
    }
}