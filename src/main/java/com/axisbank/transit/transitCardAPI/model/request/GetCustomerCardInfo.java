package com.axisbank.transit.transitCardAPI.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCustomerCardInfo {
    
    @JsonProperty("CardNo")
    private String cardNo = "";
    @JsonProperty("BankingCustomerno")
    private String bankingCustomerno = "";
    @JsonProperty("BarcodeNo")
    private String barcodeNo = "";
    @JsonProperty("CustomerName")
    private String customerName = "";
    @JsonProperty("CustomerMidName")
    private String customerMidName = "";
    @JsonProperty("CustomerSurname")
    private String customerSurname = "";
    @JsonProperty("BirthDate")
    private String birthDate = "";
    @JsonProperty("MobileNo")
    private String mobileNo = "";

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankingCustomerno() {
        return bankingCustomerno;
    }

    public void setBankingCustomerno(String bankingCustomerno) {
        this.bankingCustomerno = bankingCustomerno;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMidName() {
        return customerMidName;
    }

    public void setCustomerMidName(String customerMidName) {
        this.customerMidName = customerMidName;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }

    public void setCustomerSurname(String customerSurname) {
        this.customerSurname = customerSurname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate.replaceAll("-","");
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }



    @Override
    public String toString() {
        return "GetCustomerCardInfo{" +
                "cardNo='" + cardNo + '\'' +
                ", bankingCustomerno='" + bankingCustomerno + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerMidName='" + customerMidName + '\'' +
                ", customerSurname='" + customerSurname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
