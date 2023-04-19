package com.axisbank.transit.kmrl.model.DTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public class MetroStationAdminDTO {
    private  String stationId;

    @NotNull
    private String displayName;

    private Double distance;

    @NotNull
    private String stationCodeUp;

    @NotNull
    private String stationCodeDn;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private List<String> metroLine;

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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public List<String> getMetroLine() {
        return metroLine;
    }

    public void setMetroLine(List<String> metroLine) {
        this.metroLine = metroLine;
    }
}
