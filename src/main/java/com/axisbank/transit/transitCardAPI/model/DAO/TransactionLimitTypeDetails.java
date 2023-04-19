package com.axisbank.transit.transitCardAPI.model.DAO;

public class TransactionLimitTypeDetails {
    private String limitType;
    private String displayName;

    public TransactionLimitTypeDetails() {
    }

    public TransactionLimitTypeDetails(String limitType, String displayName) {
        this.limitType = limitType;
        this.displayName = displayName;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
