package com.axisbank.transit.bus.model.DTO;

public class BusFareAdminDTO {

    private String fareId;
    private double fare;
    private String routeCode;
    private String fromBusStationName;
    private String toBusStationName;

    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getFromBusStationName() {
        return fromBusStationName;
    }

    public void setFromBusStationName(String fromBusStationName) {
        this.fromBusStationName = fromBusStationName;
    }

    public String getToBusStationName() {
        return toBusStationName;
    }

    public void setToBusStationName(String toBusStationName) {
        this.toBusStationName = toBusStationName;
    }
}
