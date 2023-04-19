package com.axisbank.transit.bus.model.DTO;

public class BusTimeTableTypesDTO {
    private String busTimetableId;
    private String timeTableName;
    private String activeDays;
    private String currentStatus;
    private Boolean isActive;

    public String getBusTimetableId() {
        return busTimetableId;
    }

    public void setBusTimetableId(String busTimetableId) {
        this.busTimetableId = busTimetableId;
    }

    public String getTimeTableName() {
        return timeTableName;
    }

    public void setTimeTableName(String timeTableName) {
        this.timeTableName = timeTableName;
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
