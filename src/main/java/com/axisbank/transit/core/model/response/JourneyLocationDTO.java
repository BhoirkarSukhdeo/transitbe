package com.axisbank.transit.core.model.response;

public class JourneyLocationDTO {
    private String name;
    private String stationId;
    private String latitude;
    private String longitude;

    public JourneyLocationDTO() {
    }

    public JourneyLocationDTO(String name, String latitude, String longitude, String stationId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
