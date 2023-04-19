package com.axisbank.transit.transitCardAPI.model.request.matchCardAndNewCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchCardAndNewCustomer {

   private String cardNo;
   @JsonProperty("BarcodeNo")
   private String barcodeNo;
   private String cardHolderName;
   @JsonProperty("BankingCustomerNo")
   private String bankingCustomerNo;
   private CustomerInfo customerInfo;
}
