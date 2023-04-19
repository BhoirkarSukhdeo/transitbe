package com.axisbank.transit.transitCardAPI.model.request.getEntityDoc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetEntityDocRequest {

    @JsonProperty("RequestBody")
    public RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
