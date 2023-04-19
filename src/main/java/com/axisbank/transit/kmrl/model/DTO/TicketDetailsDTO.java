package com.axisbank.transit.kmrl.model.DTO;


import java.sql.Time;

public class TicketDetailsDTO {

    private String ticketType;
    private String ticketId; //which id to be shown
    private Double ticketFare;
    private String source;
    private String destination;
    private String ticketNo;
    private String lineType;
    private String tripType;
    private Time nextTripTime;
    private String journeyDate;
    private String ticketGUID;
    private String ticketRefId;
    private Double totalDistance;
    private Double totalTime;
    private String ticketStatus;
    private String ticketTypeDispName;
    private String secondaryTicketStatus;
    private String message;

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public Time getNextTripTime() {
        return nextTripTime;
    }

    public void setNextTripTime(Time nextTripTime) {
        this.nextTripTime = nextTripTime;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Double getTicketFare() {
        return ticketFare;
    }

    public void setTicketFare(Double ticketFare) {
        this.ticketFare = ticketFare;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getTicketGUID() {
        return ticketGUID;
    }

    public void setTicketGUID(String ticketGUID) {
        this.ticketGUID = ticketGUID;
    }

    public String getTicketRefId() {
        return ticketRefId;
    }

    public void setTicketRefId(String ticketRefId) {
        this.ticketRefId = ticketRefId;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketTypeDispName() {
        return ticketTypeDispName;
    }

    public void setTicketTypeDispName(String ticketTypeDispName) {
        this.ticketTypeDispName = ticketTypeDispName;
    }

    public String getSecondaryTicketStatus() {
        return secondaryTicketStatus;
    }

    public void setSecondaryTicketStatus(String secondaryTicketStatus) {
        this.secondaryTicketStatus = secondaryTicketStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
