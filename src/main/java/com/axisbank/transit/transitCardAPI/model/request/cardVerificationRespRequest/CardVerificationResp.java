package com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest;

public class CardVerificationResp {

    private String cardNoLastDigits;
    private String barcodeNo;
    private String mobileNo;

    public String getCardNoLastDigits() {
        return cardNoLastDigits;
    }

    public void setCardNoLastDigits(String cardNoLastDigits) {
        this.cardNoLastDigits = cardNoLastDigits;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public CardVerificationResp() {
    }

    public CardVerificationResp(String cardNoLastDigits, String barcodeNo, String mobileNo) {
        this.cardNoLastDigits = cardNoLastDigits;
        this.barcodeNo = barcodeNo;
        this.mobileNo = mobileNo;
    }

    @Override
    public String toString() {
        return "CardVerificationResp{" +
                "cardNoLastDigits='" + cardNoLastDigits + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
