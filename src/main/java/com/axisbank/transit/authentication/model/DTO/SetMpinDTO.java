package com.axisbank.transit.authentication.model.DTO;

public class SetMpinDTO {
    private String currentMpin;
    private String mpin;
    private String confirmMpin;

    public String getCurrentMpin() {
        return currentMpin;
    }

    public void setCurrentMpin(String currentMpin) {
        this.currentMpin = currentMpin;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public String getConfirmMpin() {
        return confirmMpin;
    }

    public void setConfirmMpin(String confirmMpin) {
        this.confirmMpin = confirmMpin;
    }
}
