package com.axisbank.transit.core.model.response;

import java.util.Date;

public class TransitCardDetailsDTO {
    private String cardNumber;
    private Date expiry;
    private String cvv;
    private String nameOnCard;

    public TransitCardDetailsDTO() {
    }

    public TransitCardDetailsDTO(String cardNumber, Date expiry, String cvv, String nameOnCard) {
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cvv = cvv;
        this.nameOnCard = nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }
}
