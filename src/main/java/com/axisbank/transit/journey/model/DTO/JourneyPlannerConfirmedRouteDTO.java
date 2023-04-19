package com.axisbank.transit.journey.model.DTO;

import java.sql.Time;
import java.util.List;

public class JourneyPlannerConfirmedRouteDTO {
    private double amount;
    private Double totalDistance;
    private long totalDuration;
    private Time departureTime;
    private Time arrivalTime ;
    private List<JourneyModeDetailsDTO> journeyModeDetails;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<JourneyModeDetailsDTO> getJourneyModeDetails() {
        return journeyModeDetails;
    }

    public void setJourneyModeDetails(List<JourneyModeDetailsDTO> journeyModeDetails) {
        this.journeyModeDetails = journeyModeDetails;
    }
}
