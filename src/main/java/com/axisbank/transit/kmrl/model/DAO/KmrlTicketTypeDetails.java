package com.axisbank.transit.kmrl.model.DAO;

public class KmrlTicketTypeDetails {
    private String displayName;
    private String ticketTypeCode;
    private Integer ticketTypeId;

    public KmrlTicketTypeDetails() {
    }

    public KmrlTicketTypeDetails(String displayName, String ticketTypeCode, Integer ticketTypeId) {
        this.displayName = displayName;
        this.ticketTypeCode = ticketTypeCode;
        this.ticketTypeId = ticketTypeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTicketTypeCode() {
        return ticketTypeCode;
    }

    public void setTicketTypeCode(String ticketTypeCode) {
        this.ticketTypeCode = ticketTypeCode;
    }

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }
}
