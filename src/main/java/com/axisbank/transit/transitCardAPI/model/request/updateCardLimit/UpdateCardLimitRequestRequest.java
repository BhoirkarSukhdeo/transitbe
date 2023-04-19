package com.axisbank.transit.transitCardAPI.model.request.updateCardLimit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCardLimitRequestRequest {

    @JsonProperty("requestBody")
    public RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
