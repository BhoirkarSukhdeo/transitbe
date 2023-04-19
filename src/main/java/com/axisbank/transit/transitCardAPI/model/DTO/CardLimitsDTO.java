package com.axisbank.transit.transitCardAPI.model.DTO;

import java.util.Date;

public class CardLimitsDTO {
    private String totalCardBalanceLimit;
    private String fullKYCLimit;
    private String eBalanceLimit;
    private LimitTypeDetailDTO chipBalanceLimit;
    private LimitTypeDetailDTO retailPOSContactlessLimit;
    private LimitTypeDetailDTO retailPOSContactLimit;
    private LimitTypeDetailDTO onlineSpendsLimit;
    private boolean isFullKYC = true;

    public String getTotalCardBalanceLimit() {
        return totalCardBalanceLimit;
    }

    public void setTotalCardBalanceLimit(String totalCardBalanceLimit) {
        this.totalCardBalanceLimit = totalCardBalanceLimit;
    }

    public String getFullKYCLimit() {
        return fullKYCLimit;
    }

    public void setFullKYCLimit(String fullKYCLimit) {
        this.fullKYCLimit = fullKYCLimit;
    }

    public String geteBalanceLimit() {
        return eBalanceLimit;
    }

    public void seteBalanceLimit(String eBalanceLimit) {
        this.eBalanceLimit = eBalanceLimit;
    }

    public LimitTypeDetailDTO getChipBalanceLimit() {
        return chipBalanceLimit;
    }

    public void setChipBalanceLimit(LimitTypeDetailDTO chipBalanceLimit) {
        this.chipBalanceLimit = chipBalanceLimit;
    }

    public LimitTypeDetailDTO getRetailPOSContactlessLimit() {
        return retailPOSContactlessLimit;
    }

    public void setRetailPOSContactlessLimit(LimitTypeDetailDTO retailPOSContactlessLimit) {
        this.retailPOSContactlessLimit = retailPOSContactlessLimit;
    }

    public LimitTypeDetailDTO getRetailPOSContactLimit() {
        return retailPOSContactLimit;
    }

    public void setRetailPOSContactLimit(LimitTypeDetailDTO retailPOSContactLimit) {
        this.retailPOSContactLimit = retailPOSContactLimit;
    }

    public LimitTypeDetailDTO getOnlineSpendsLimit() {
        return onlineSpendsLimit;
    }

    public void setOnlineSpendsLimit(LimitTypeDetailDTO onlineSpendsLimit) {
        this.onlineSpendsLimit = onlineSpendsLimit;
    }

    public boolean isFullKYC() {
        return isFullKYC;
    }

    public void setFullKYC(boolean fullKYC) {
        isFullKYC = fullKYC;
    }
}
