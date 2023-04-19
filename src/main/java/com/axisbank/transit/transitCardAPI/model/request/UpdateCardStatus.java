package com.axisbank.transit.transitCardAPI.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCardStatus {

    private String cardNo;
    @JsonProperty("BarcodeNo")
    private String barcodeNo;
    private String status;
    private String subStatus;
    private String cancelReasonCode;
    private String description;
    private String embossType;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getCancelReasonCode() {
        return cancelReasonCode;
    }

    public void setCancelReasonCode(String cancelReasonCode) {
        this.cancelReasonCode = cancelReasonCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmbossType() {
        return embossType;
    }

    public void setEmbossType(String embossType) {
        this.embossType = embossType;
    }

    @Override
    public String toString() {
        return "UpdateCardStatus{" +
                "cardNo='" + cardNo + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", status='" + status + '\'' +
                ", subStatus='" + subStatus + '\'' +
                ", cancelReasonCode='" + cancelReasonCode + '\'' +
                ", description='" + description + '\'' +
                ", embossType='" + embossType + '\'' +
                '}';
    }
}
