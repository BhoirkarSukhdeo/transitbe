package com.axisbank.transit.bus.model.DTO;

import javax.validation.constraints.NotNull;

public class BusStationDTO {
    private  String stationId;
    @NotNull
    private String displayName;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

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
}
