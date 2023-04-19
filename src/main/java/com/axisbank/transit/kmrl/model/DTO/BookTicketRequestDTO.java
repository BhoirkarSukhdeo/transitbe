package com.axisbank.transit.kmrl.model.DTO;

public class BookTicketRequestDTO {
    private String fromStationId;
    private String toStationId;
    private String ticketType;
    private short travellers;
    private String paymentMethod;

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

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public short getTravellers() {
        return travellers;
    }

    public void setTravellers(short travellers) {
        this.travellers = travellers;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
