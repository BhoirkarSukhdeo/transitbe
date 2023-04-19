package com.axisbank.transit.bus.model.DTO;

import java.sql.Time;

public class BusTimetableAdminDTO {
    private String timeTableId;
    private Time arivalTime;
    private Time departureTime;
    private int tripNumber;
    private String routeType;
    private int srNum;
    private String routeCode;
    private String busStationName;

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

    public int getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(int tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public int getSrNum() {
        return srNum;
    }

    public void setSrNum(int srNum) {
        this.srNum = srNum;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getBusStationName() {
        return busStationName;
    }

    public void setBusStationName(String busStationName) {
        this.busStationName = busStationName;
    }
}
