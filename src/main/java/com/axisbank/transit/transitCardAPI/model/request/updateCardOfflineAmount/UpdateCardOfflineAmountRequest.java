package com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCardOfflineAmountRequest {

    @JsonProperty("requestBody")
    public RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
