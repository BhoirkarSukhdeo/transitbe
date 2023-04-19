package com.axisbank.transit.core.model.response;

public class AppConfigDTO {
    private String googleApiKey;
    private int ticketRefreshInterval;

    public AppConfigDTO() {
    }

    public AppConfigDTO(String googleApiKey, int ticketRefreshInterval) {
        this.googleApiKey = googleApiKey;
        this.ticketRefreshInterval = ticketRefreshInterval;
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public void setGoogleApiKey(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    public int getTicketRefreshInterval() {
        return ticketRefreshInterval;
    }

    public void setTicketRefreshInterval(int ticketRefreshInterval) {
        this.ticketRefreshInterval = ticketRefreshInterval;
    }
}
