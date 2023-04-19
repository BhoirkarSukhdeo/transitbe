package com.axisbank.transit.transitCardAPI.model.DTO;

public class TransitCardInfoDTO {

    private String totalChipBalance;
    private String totalHostBalance;
    private String totalCardBalance;
    private String expiryDate;
    private String cardNo;
    private String cardType;
    private String cardStatCode;
    private String cardStatSubCode;
    private String embossName;
    private String customerNo;
    private String barcodeNum;

    public String getTotalChipBalance() {
        return totalChipBalance;
    }

    public void setTotalChipBalance(String totalChipBalance) {
        this.totalChipBalance = totalChipBalance;
    }

    public String getTotalHostBalance() {
        return totalHostBalance;
    }

    public void setTotalHostBalance(String totalHostBalance) {
        this.totalHostBalance = totalHostBalance;
    }

    public String getTotalCardBalance() {
        return totalCardBalance;
    }

    public void setTotalCardBalance(String totalCardBalance) {
        this.totalCardBalance = totalCardBalance;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardStatCode() {
        return cardStatCode;
    }

    public void setCardStatCode(String cardStatCode) {
        this.cardStatCode = cardStatCode;
    }

    public String getCardStatSubCode() {
        return cardStatSubCode;
    }

    public void setCardStatSubCode(String cardStatSubCode) {
        this.cardStatSubCode = cardStatSubCode;
    }

    public String getEmbossName() {
        return embossName;
    }

    public void setEmbossName(String embossName) {
        this.embossName = embossName;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getBarcodeNum() {
        return barcodeNum;
    }

    public void setBarcodeNum(String barcodeNum) {
        this.barcodeNum = barcodeNum;
    }
}
