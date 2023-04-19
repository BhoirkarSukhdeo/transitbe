package com.axisbank.transit.bus.model.DTO;

import javax.validation.constraints.NotNull;

public class BusFareUpdateDTO {
    private String fareId;
    @NotNull
    private double fare;

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
}
