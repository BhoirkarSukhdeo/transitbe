package com.axisbank.transit.kmrl.model.DTO;

import java.sql.Time;

public class MetroTimeTableAdminDTO {
    private String timeTableId;
    private Time arivalTime;
    private Time departureTime;
    private long srNum;
    private long dwelltime;
    private long prevRuntime;
    private long totalRuntime;
    private String stationName;
    private String metroLineName;
    private String metroTripNum;
    private String direction;
    private String totalDistance;

    public String getTimeTableId() {
        return timeTableId;
    }

    public void setTimeTableId(String timeTableId) {
        this.timeTableId = timeTableId;
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

    public long getSrNum() {
        return srNum;
    }

    public void setSrNum(long srNum) {
        this.srNum = srNum;
    }

    public long getDwelltime() {
        return dwelltime;
    }

    public void setDwelltime(long dwelltime) {
        this.dwelltime = dwelltime;
    }

    public long getPrevRuntime() {
        return prevRuntime;
    }

    public void setPrevRuntime(long prevRuntime) {
        this.prevRuntime = prevRuntime;
    }

    public long getTotalRuntime() {
        return totalRuntime;
    }

    public void setTotalRuntime(long totalRuntime) {
        this.totalRuntime = totalRuntime;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getMetroLineName() {
        return metroLineName;
    }

    public void setMetroLineName(String metroLineName) {
        this.metroLineName = metroLineName;
    }

    public String getMetroTripNum() {
        return metroTripNum;
    }

    public void setMetroTripNum(String metroTripNum) {
        this.metroTripNum = metroTripNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }
}
