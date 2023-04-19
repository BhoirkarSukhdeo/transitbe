package com.axisbank.transit.kmrl.model.DTO;

import java.sql.Time;

public class MetroTimetableStationDTO {
    private  String stationId;
    private String displayName;
    private Double latitude;
    private Double longitude;
    private long srNum;
    private Time arivalTime;
    private Time departureTime;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getSrNum() {
        return srNum;
    }

    public void setSrNum(long srNum) {
        this.srNum = srNum;
    }

    public Time getArivalTime() {
        return arivalTime;
    }

    public void setArivalTime(Time arivalTime) {
        this.arivalTime = arivalTime;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }
}
