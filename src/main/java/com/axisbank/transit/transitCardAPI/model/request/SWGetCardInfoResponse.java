package com.axisbank.transit.transitCardAPI.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SWGetCardInfoResponse {
   
    @JsonProperty("CustomerNo")
    private String customerNo="";
    @JsonProperty("CardNo")
    private String cardNo="";
    @JsonProperty("BarcodeNo")
    private String barcodeNo="";
    @JsonProperty("CardDci")
    private String cardDci="All";
    @JsonProperty("CustomerGSMNo")
    private String customerGSMNo="";
    @JsonProperty("CustomerName")
    private String customerName="";
    @JsonProperty("CustomerMidleName")
    private String customerMidleName="";
    @JsonProperty("CustomerSurname")
    private String customerSurname="";

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getCardDci() {
        return cardDci;
    }

    public void setCardDci(String cardDci) {
        this.cardDci = cardDci;
    }

    public String getCustomerGSMNo() {
        return customerGSMNo;
    }

    public void setCustomerGSMNo(String customerGSMNo) {
        this.customerGSMNo = customerGSMNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMidleName() {
        return customerMidleName;
    }

    public void setCustomerMidleName(String customerMidleName) {
        this.customerMidleName = customerMidleName;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }

    public void setCustomerSurname(String customerSurname) {
        this.customerSurname = customerSurname;
    }

    @Override
    public String toString() {
        return "SWGetCardInfoResponse{" +
                "customerNo='" + customerNo + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", cardDci='" + cardDci + '\'' +
                ", customerGSMNo='" + customerGSMNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerMidleName='" + customerMidleName + '\'' +
                ", customerSurname='" + customerSurname + '\'' +
                '}';
    }
}
