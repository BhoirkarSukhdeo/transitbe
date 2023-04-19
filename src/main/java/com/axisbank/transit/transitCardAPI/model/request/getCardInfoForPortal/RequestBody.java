package com.axisbank.transit.transitCardAPI.model.request.getCardInfoForPortal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestBody {

    @JsonProperty("BarcodeNo")
    private String barcodeNo;
    @JsonProperty("BankingCustomerNo")
    private String bankingCustomerNo;
    @JsonProperty("CustomerNo")
    private String customerNo;


}
