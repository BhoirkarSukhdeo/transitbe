package com.axisbank.transit.payment.model.response;

public class PaymentTransactionStatus {
    private String orderId;
    private String pspTxnId;
    private String spTxnId;
    private String amount;
    private String date;
    private String paymentMethod;
    private String paidTo;
    private String time;
    private String status;

    public PaymentTransactionStatus() {
    }

    public PaymentTransactionStatus(String orderId, String pspTxnId, String spTxnId, String amount, String date, String paymentMethod, String paidTo, String time, String status) {
        this.orderId = orderId;
        this.pspTxnId = pspTxnId;
        this.spTxnId = spTxnId;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.paidTo = paidTo;
        this.time = time;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPspTxnId() {
        return pspTxnId;
    }

    public void setPspTxnId(String pspTxnId) {
        this.pspTxnId = pspTxnId;
    }

    public String getSpTxnId() {
        return spTxnId;
    }

    public void setSpTxnId(String spTxnId) {
        this.spTxnId = spTxnId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public void setPaidTo(String paidTo) {
        this.paidTo = paidTo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
