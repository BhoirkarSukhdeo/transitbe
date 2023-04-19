package com.axisbank.transit.bus.model.DTO;

public class NearByBusStationsDTO {
    private String displayName;
    private String stationId;
    private Double latitude;
    private Double longitude;
    private Double duration;
    private Double osrmDistance;
    private Double osrmDuration;
    private Double sqlDist;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
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

    public Double getOsrmDistance() {
        return osrmDistance;
    }

    public void setOsrmDistance(Double osrmDistance) {
        this.osrmDistance = osrmDistance;
    }

    public Double getOsrmDuration() {
        return osrmDuration;
    }

    public void setOsrmDuration(Double osrmDuration) {
        this.osrmDuration = osrmDuration;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getSqlDist() {
        return sqlDist;
    }

    public void setSqlDist(Double sqlDistance) {
        this.sqlDist = sqlDistance;
    }

    @Override
    public String toString() {
        return "NearByStationsDTO{" +
                "name='" + displayName + '\'' +
                ", stationId='" + stationId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", duration=" + duration +
                ", osrmDistance=" + osrmDistance +
                ", osrmDuration=" + osrmDuration +
                ", sqlDistance=" + sqlDist +
                '}';
    }
}
