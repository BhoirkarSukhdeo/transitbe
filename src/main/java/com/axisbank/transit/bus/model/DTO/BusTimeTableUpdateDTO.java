package com.axisbank.transit.bus.model.DTO;

import javax.validation.constraints.NotNull;
import java.sql.Time;

public class BusTimeTableUpdateDTO {
    private String timeTableId;
    @NotNull
    private Time arivalTime;
    @NotNull
    private Time departureTime;

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
}
