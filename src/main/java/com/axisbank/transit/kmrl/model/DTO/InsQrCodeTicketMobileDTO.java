package com.axisbank.transit.kmrl.model.DTO;

public class InsQrCodeTicketMobileDTO {
    private String activeFrom;
    private String activeTo;
    private String explanation;
    private int FromId;
    private short peopleCount;
    private double price;
    private int ticketType;
    private int toId;
    private int weekendPassType;

    public String getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(String activeFrom) {
        this.activeFrom = activeFrom;
    }

    public String getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(String activeTo) {
        this.activeTo = activeTo;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getFromId() {
        return FromId;
    }

    public void setFromId(int fromId) {
        FromId = fromId;
    }

    public short getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(short peopleCount) {
        this.peopleCount = peopleCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTicketType() {
        return ticketType;
    }

    public void setTicketType(int ticketType) {
        this.ticketType = ticketType;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public int getWeekendPassType() {
        return weekendPassType;
    }

    public void setWeekendPassType(int weekendPassType) {
        this.weekendPassType = weekendPassType;
    }
}
