package com.axisbank.transit.journey.model.DTO;

public class CoordinatesDto {
    private double latitude;
    private double longitude;

    public CoordinatesDto(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CoordinatesDto() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
