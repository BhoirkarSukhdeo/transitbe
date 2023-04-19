package com.axisbank.transit.transitCardAPI.model.request.topupToCardWithLastFourDigits;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupServiceRequest {

    @JsonProperty("AllowedProcessChannel")
    private String allowedProcessChannel;
    @JsonProperty("BalanceType")
    private String balanceType;
    @JsonProperty("CardNoLastFourDigits")
    private String cardNoLastFourDigits;
    @JsonProperty("MobileNo")
    private String mobileNo;
    @JsonProperty("BarcodeNo")
    private String barcodeNo;
    @JsonProperty("InsertChannel")
    private String insertChannel;
    @JsonProperty("ShowInStmt")
    private String showInStmt;
    @JsonProperty("SlipNo")
    private String slipNo;
    @JsonProperty("StmtMsg")
    private String stmtMsg;
    @JsonProperty("TransactionType")
    private String transactionType;
    @JsonProperty("TxnAmnt")
    private String txnAmnt;
    @JsonProperty("TxnCurrCode")
    private String txnCurrCode;
    @JsonProperty("TxnStt")
    private String txnStt;
    @JsonProperty("TxnTrm")
    private String txnTrm;
    @JsonProperty("SrcRefId")
    private String srcRefId;
    @JsonProperty("Otc")
    private String otc;
    @JsonProperty("Ots")
    private String ots;
    @JsonProperty("Ote")
    private String ote;
    @JsonProperty("IbanNo")
    private String ibanNo;


    public String getAllowedProcessChannel() {
        return allowedProcessChannel;
    }

    public void setAllowedProcessChannel(String allowedProcessChannel) {
        this.allowedProcessChannel = allowedProcessChannel;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public String getCardNoLastFourDigits() {
        return cardNoLastFourDigits;
    }

    public void setCardNoLastFourDigits(String cardNoLastFourDigits) {
        this.cardNoLastFourDigits = cardNoLastFourDigits;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getInsertChannel() {
        return insertChannel;
    }

    public void setInsertChannel(String insertChannel) {
        this.insertChannel = insertChannel;
    }

    public String getShowInStmt() {
        return showInStmt;
    }

    public void setShowInStmt(String showInStmt) {
        this.showInStmt = showInStmt;
    }

    public String getSlipNo() {
        return slipNo;
    }

    public void setSlipNo(String slipNo) {
        this.slipNo = slipNo;
    }

    public String getStmtMsg() {
        return stmtMsg;
    }

    public void setStmtMsg(String stmtMsg) {
        this.stmtMsg = stmtMsg;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTxnAmnt() {
        return txnAmnt;
    }

    public void setTxnAmnt(String txnAmnt) {
        this.txnAmnt = txnAmnt;
    }

    public String getTxnCurrCode() {
        return txnCurrCode;
    }

    public void setTxnCurrCode(String txnCurrCode) {
        this.txnCurrCode = txnCurrCode;
    }

    public String getTxnStt() {
        return txnStt;
    }

    public void setTxnStt(String txnStt) {
        this.txnStt = txnStt;
    }

    public String getTxnTrm() {
        return txnTrm;
    }

    public void setTxnTrm(String txnTrm) {
        this.txnTrm = txnTrm;
    }

    public String getSrcRefId() {
        return srcRefId;
    }

    public void setSrcRefId(String srcRefId) {
        this.srcRefId = srcRefId;
    }

    public String getOtc() {
        return otc;
    }

    public void setOtc(String otc) {
        this.otc = otc;
    }

    public String getOts() {
        return ots;
    }

    public void setOts(String ots) {
        this.ots = ots;
    }

    public String getOte() {
        return ote;
    }

    public void setOte(String ote) {
        this.ote = ote;
    }

    public String getIbanNo() {
        return ibanNo;
    }

    public void setIbanNo(String ibanNo) {
        this.ibanNo = ibanNo;
    }

    @Override
    public String toString() {
        return "TopupServiceRequest{" +
                "allowedProcessChannel='" + allowedProcessChannel + '\'' +
                ", balanceType='" + balanceType + '\'' +
                ", cardNoLastFourDigits='" + cardNoLastFourDigits + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", insertChannel='" + insertChannel + '\'' +
                ", showInStmt='" + showInStmt + '\'' +
                ", slipNo='" + slipNo + '\'' +
                ", stmtMsg='" + stmtMsg + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", txnAmnt='" + txnAmnt + '\'' +
                ", txnCurrCode='" + txnCurrCode + '\'' +
                ", txnStt='" + txnStt + '\'' +
                ", txnTrm='" + txnTrm + '\'' +
                ", srcRefId='" + srcRefId + '\'' +
                ", otc='" + otc + '\'' +
                ", ots='" + ots + '\'' +
                ", ote='" + ote + '\'' +
                ", ibanNo='" + ibanNo + '\'' +
                '}';
    }
}
