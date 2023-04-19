package com.axisbank.transit.transitCardAPI.model.request.getCustomerDtlsRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCustomerDtlsRequest {

    @JsonProperty("Cust_Id")
    public String custId;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}
