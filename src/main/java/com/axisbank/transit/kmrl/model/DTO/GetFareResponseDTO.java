package com.axisbank.transit.kmrl.model.DTO;

import com.axisbank.transit.kmrl.model.DAO.KmrlTicketTypeDetails;

import java.util.List;

public class GetFareResponseDTO {
    private String journeyDate;
    private String ticketType;
    private String fromStationId;
    private String toStationId;
    private String toStationDisplayName;
    private String fromStationDisplayName;
    private String metroLine;
    private String ticketFare;
    private List<PaymentMethodsDTO> paymentMethods;
    private List<KmrlTicketTypeDetails> ticketTypes;

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(String fromStationId) {
        this.fromStationId = fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public void setToStationId(String toStationId) {
        this.toStationId = toStationId;
    }

    public String getToStationDisplayName() {
        return toStationDisplayName;
    }

    public void setToStationDisplayName(String toStationDisplayName) {
        this.toStationDisplayName = toStationDisplayName;
    }

    public String getFromStationDisplayName() {
        return fromStationDisplayName;
    }

    public void setFromStationDisplayName(String fromStationDisplayName) {
        this.fromStationDisplayName = fromStationDisplayName;
    }

    public String getMetroLine() {
        return metroLine;
    }

    public void setMetroLine(String metroLine) {
        this.metroLine = metroLine;
    }

    public List<PaymentMethodsDTO> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodsDTO> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getTicketFare() {
        return ticketFare;
    }

    public void setTicketFare(String ticketFare) {
        this.ticketFare = ticketFare;
    }

    public List<KmrlTicketTypeDetails> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(List<KmrlTicketTypeDetails> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }
}
