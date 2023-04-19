package com.axisbank.transit.payment.model.DTO;

public class TransactionAdminDetailsDTO {
    private String userId;
    private String mobile;
    private String date;
    private String time;
    private String txnType;
    private String txnId;
    private String refundId;
    private String topUpType;
    private String ticketType;
    private String autoBookingType;
    private String billTo;
    private double transactionAmount;
    private String paymentMode;
    private String paymentStatus;
    private String paymentGatewayTxnId;
    private String transactionStatus;
    private String proceedTxnId;
    private String source;
    private String destination;
    private String couponId;
    private double payableAmount;
    private String transactionReferenceNumber;
    private String authCode;
    private String merchantCoupon;
    private String afcTxnId;
    private String userName;
    private String merchantId;

    public TransactionAdminDetailsDTO() {
    }

    public TransactionAdminDetailsDTO(String userId, String mobile, String date, String time, String txnType, String txnId,
                                      String refundId, String topUpType, String ticketType, String autoBookingType,
                                      String billTo, double transactionAmount, String paymentMode, String paymentStatus,
                                      String paymentGatewayTxnId, String transactionStatus, String proceedTxnId,
                                      String source, String destination, String couponId, double payableAmount,
                                      String transactionReferenceNumber, String authCode, String merchantCoupon,
                                      String afcTxnId, String userName, String merchantId) {
        this.userId = userId;
        this.mobile = mobile;
        this.date = date;
        this.time = time;
        this.txnType = txnType;
        this.txnId = txnId;
        this.refundId = refundId;
        this.topUpType = topUpType;
        this.ticketType = ticketType;
        this.autoBookingType = autoBookingType;
        this.billTo = billTo;
        this.transactionAmount = transactionAmount;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.paymentGatewayTxnId = paymentGatewayTxnId;
        this.transactionStatus = transactionStatus;
        this.proceedTxnId = proceedTxnId;
        this.source = source;
        this.destination = destination;
        this.couponId = couponId;
        this.payableAmount = payableAmount;
        this.transactionReferenceNumber = transactionReferenceNumber;
        this.authCode = authCode;
        this.merchantCoupon = merchantCoupon;
        this.afcTxnId = afcTxnId;
        this.userName = userName;
        this.merchantId=merchantId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getAutoBookingType() {
        return autoBookingType;
    }

    public void setAutoBookingType(String autoBookingType) {
        this.autoBookingType = autoBookingType;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTopUpType() {
        return topUpType;
    }

    public void setTopUpType(String topUpType) {
        this.topUpType = topUpType;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentGatewayTxnId() {
        return paymentGatewayTxnId;
    }

    public void setPaymentGatewayTxnId(String paymentGatewayTxnId) {
        this.paymentGatewayTxnId = paymentGatewayTxnId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getProceedTxnId() {
        return proceedTxnId;
    }

    public void setProceedTxnId(String proceedTxnId) {
        this.proceedTxnId = proceedTxnId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getMerchantCoupon() {
        return merchantCoupon;
    }

    public void setMerchantCoupon(String merchantCoupon) {
        this.merchantCoupon = merchantCoupon;
    }

    public String getAfcTxnId() {
        return afcTxnId;
    }

    public void setAfcTxnId(String afcTxnId) {
        this.afcTxnId = afcTxnId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
