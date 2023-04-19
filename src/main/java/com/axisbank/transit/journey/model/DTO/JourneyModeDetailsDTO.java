package com.axisbank.transit.journey.model.DTO;

public class JourneyModeDetailsDTO extends JourneyModeDetails{
    private Boolean isTicketBooked;
    private String ticketId;
    private Boolean isBookingAllowed;

    public Boolean getTicketBooked() {
        return isTicketBooked;
    }

    public void setTicketBooked(Boolean ticketBooked) {
        isTicketBooked = ticketBooked;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Boolean getBookingAllowed() {
        return isBookingAllowed;
    }

    public void setBookingAllowed(Boolean bookingAllowed) {
        isBookingAllowed = bookingAllowed;
    }
}
