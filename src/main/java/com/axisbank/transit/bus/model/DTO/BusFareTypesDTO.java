package com.axisbank.transit.bus.model.DTO;

public class BusFareTypesDTO {
    private String busFareTypeId;
    private String busFareName;
    private String activeDays;
    private String currentStatus;
    private Boolean isActive;

    public String getBusFareTypeId() {
        return busFareTypeId;
    }

    public void setBusFareTypeId(String busFareTypeId) {
        this.busFareTypeId = busFareTypeId;
    }

    public String getBusFareName() {
        return busFareName;
    }

    public void setBusFareName(String busFareName) {
        this.busFareName = busFareName;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
