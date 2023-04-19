package com.axisbank.transit.bus.model.DTO;

public class StationFareDTO {
    private String stationName;
    private Double fare;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }
}
