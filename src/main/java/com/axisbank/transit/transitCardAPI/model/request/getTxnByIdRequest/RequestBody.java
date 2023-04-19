package com.axisbank.transit.transitCardAPI.model.request.getTxnByIdRequest;

public class RequestBody {

    private String txnReferanceId;
    private String cardNo;
    private String insertChannel;

    public String getTxnReferanceId() {
        return txnReferanceId;
    }

    public void setTxnReferanceId(String txnReferanceId) {
        this.txnReferanceId = txnReferanceId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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
                "txnReferanceId='" + txnReferanceId + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", insertChannel='" + insertChannel + '\'' +
                '}';
    }
}
