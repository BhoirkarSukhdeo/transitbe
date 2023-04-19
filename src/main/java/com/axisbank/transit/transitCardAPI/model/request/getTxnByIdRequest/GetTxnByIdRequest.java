package com.axisbank.transit.transitCardAPI.model.request.getTxnByIdRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetTxnByIdRequest {

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
        return "GetTxnByIdRequest{" +
                "requestBody=" + requestBody +
                '}';
    }
}


