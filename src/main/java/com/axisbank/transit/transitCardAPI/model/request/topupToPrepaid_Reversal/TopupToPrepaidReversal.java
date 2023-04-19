package com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupToPrepaidReversal {

    @JsonProperty("RequestBody")
    public RequestBodyReversal requestBodyReversal;

    public RequestBodyReversal getRequestBodyReversal() {
        return requestBodyReversal;
    }

    public void setRequestBodyReversal(RequestBodyReversal requestBodyReversal) {
        this.requestBodyReversal = requestBodyReversal;
    }

    @Override
    public String toString() {
        return "TopupToPrepaidReversal{" +
                "requestBody=" + requestBodyReversal +
                '}';
    }
}
