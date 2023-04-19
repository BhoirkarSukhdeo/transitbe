package com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestBodyReversal {

    @JsonProperty("CardNo")
    private String cardNo="";
    @JsonProperty("BarcodeNo")
    private String barcodeNo="";
    @JsonProperty("TxnReferanceId")
    private String txnReferanceId="";
    @JsonProperty("InsertChannel")
    private String insertChannel="";

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

    public String getTxnReferanceId() {
        return txnReferanceId;
    }

    public void setTxnReferanceId(String txnReferanceId) {
        this.txnReferanceId = txnReferanceId;
    }

    public String getInsertChannel() {
        return insertChannel;
    }

    public void setInsertChannel(String insertChannel) {
        this.insertChannel = insertChannel;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "cardNo='" + cardNo + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", txnReferanceId='" + txnReferanceId + '\'' +
                ", insertChannel='" + insertChannel + '\'' +
                '}';
    }
}
