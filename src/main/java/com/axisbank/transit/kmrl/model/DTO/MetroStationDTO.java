package com.axisbank.transit.kmrl.model.DTO;

public class MetroStationDTO{

    private  String stationId;

    private String displayName;

    private Double distance;

    private String stationCodeUp;

    private String stationCodeDn;

    private Double latitude;

    private Double longitude;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStationCodeUp() {
        return stationCodeUp;
    }

    public void setStationCodeUp(String stationCodeUp) {
        this.stationCodeUp = stationCodeUp;
    }

    public String getStationCodeDn() {
        return stationCodeDn;
    }

    public void setStationCodeDn(String stationCodeDn) {
        this.stationCodeDn = stationCodeDn;
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

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
