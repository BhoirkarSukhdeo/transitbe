package com.axisbank.transit.transitCardAPI.model.request;

public class TopupRequest {

    private String amount;
    private String srcRefId;
    public TopupRequest() {
    }
    public TopupRequest(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSrcRefId() {
        return srcRefId;
    }

    public void setSrcRefId(String srcRefId) {
        this.srcRefId = srcRefId;
    }
}
