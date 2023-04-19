package com.axisbank.transit.transitCardAPI.model.request.CustExistCheck;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustExistCheckRequest {

    @JsonProperty("ReferenceId")
    private String referenceId="";
    @JsonProperty("Name")
    private String name="";
    @JsonProperty("MobileNo")
    private String mobileNo="";
    @JsonProperty("DateofBirth")
    private String dateofBirth="";
    @JsonProperty("CustomerId")
    private String customerId="";
    @JsonProperty("FinacleId")
    private String finacleId="";

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFinacleId() {
        return finacleId;
    }

    public void setFinacleId(String finacleId) {
        this.finacleId = finacleId;
    }

    @Override
    public String toString() {
        return "CustExistCheckRequest{" +
                "referenceId='" + referenceId + '\'' +
                ", name='" + name + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", dateofBirth='" + dateofBirth + '\'' +
                ", customerId='" + customerId + '\'' +
                ", finacleId='" + finacleId + '\'' +
                '}';
    }
}
