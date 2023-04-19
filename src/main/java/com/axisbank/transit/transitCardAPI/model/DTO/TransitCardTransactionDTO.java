package com.axisbank.transit.transitCardAPI.model.DTO;

import java.util.Date;

public class TransitCardTransactionDTO {
    private String txnId;
    private String transitCardRefId;
    private String pgRefId;
    private String pgTxnStatus;
    private String transitCardTxnStatus;
    private String txnStatus;
    private String txnType;
    private String amount;
    private String typeOfPayement;
    private String methodPayement;
    private Date updatedAt;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTransitCardRefId() {
        return transitCardRefId;
    }

    public void setTransitCardRefId(String transitCardRefId) {
        this.transitCardRefId = transitCardRefId;
    }

    public String getPgRefId() {
        return pgRefId;
    }

    public void setPgRefId(String pgRefId) {
        this.pgRefId = pgRefId;
    }

    public String getPgTxnStatus() {
        return pgTxnStatus;
    }

    public void setPgTxnStatus(String pgTxnStatus) {
        this.pgTxnStatus = pgTxnStatus;
    }

    public String getTransitCardTxnStatus() {
        return transitCardTxnStatus;
    }

    public void setTransitCardTxnStatus(String transitCardTxnStatus) {
        this.transitCardTxnStatus = transitCardTxnStatus;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTypeOfPayement() {
        return typeOfPayement;
    }

    public void setTypeOfPayement(String typeOfPayement) {
        this.typeOfPayement = typeOfPayement;
    }

    public String getMethodPayement() {
        return methodPayement;
    }

    public void setMethodPayement(String methodPayement) {
        this.methodPayement = methodPayement;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}