package com.axisbank.transit.transitCardAPI.model.request.availableLimit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableLimitRequest {

    @JsonProperty("ReferenceId")
    private String referenceId="";

    @JsonProperty("UniqueCustomerId")
    private String uniqueCustomerId="";

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getUniqueCustomerId() {
        return uniqueCustomerId;
    }

    public void setUniqueCustomerId(String uniqueCustomerId) {
        this.uniqueCustomerId = uniqueCustomerId;
    }
}
