package com.axisbank.transit.transitCardAPI.model.request.topupToCardWithLastFourDigits;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupToCardWithLastFourDigits {

    @JsonProperty("topupServiceRequest")
    private TopupServiceRequest topupServiceRequest;

    public TopupServiceRequest getTopupServiceRequest() {
        return topupServiceRequest;
    }

    public void setTopupServiceRequest(TopupServiceRequest topupServiceRequest) {
        this.topupServiceRequest = topupServiceRequest;
    }
}
