package com.axisbank.transit.kmrl.model.DTO;

import in.juspay.model.PaymentLinks;

public class BookTicketResponseDTO {

    private String orderId;
    private PaymentLinks paymentLinks;
    private String methodMethodType;
    private String bookingStatus;// (spStatus) -- null then Booked
    private String transactionStatus; // (finalTxn)
    private String bookingId; // (TickeTRefId)

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PaymentLinks getPaymentLinks() {
        return paymentLinks;
    }

    public void setPaymentLinks(PaymentLinks paymentLinks) {
        this.paymentLinks = paymentLinks;
    }

    public String getMethodMethodType() {
        return methodMethodType;
    }

    public void setMethodMethodType(String methodMethodType) {
        this.methodMethodType = methodMethodType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
