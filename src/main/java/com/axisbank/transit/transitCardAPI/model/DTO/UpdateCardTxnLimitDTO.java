package com.axisbank.transit.transitCardAPI.model.DTO;

public class UpdateCardTxnLimitDTO {
    private String limitType;
    private double amount;
    private boolean enabled;

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
