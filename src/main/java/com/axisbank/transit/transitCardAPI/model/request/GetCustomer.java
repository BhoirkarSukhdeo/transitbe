package com.axisbank.transit.transitCardAPI.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCustomer {

    @JsonProperty("CustomerNo")
    private String customerNo;

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    @Override
    public String toString() {
        return "GetCustomer{" +
                "customerNo='" + customerNo + '\'' +
                '}';
    }
}
