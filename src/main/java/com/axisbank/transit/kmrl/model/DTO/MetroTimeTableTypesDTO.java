package com.axisbank.transit.kmrl.model.DTO;

public class MetroTimeTableTypesDTO {
    private String mtTimetableId;
    private String timeTableName;
    private String activeDays;
    private String currentStatus;
    private Boolean isActive;

    public String getMtTimetableId() {
        return mtTimetableId;
    }

    public void setMtTimetableId(String mtTimetableId) {
        this.mtTimetableId = mtTimetableId;
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
