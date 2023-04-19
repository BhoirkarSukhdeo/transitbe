package com.axisbank.transit.userDetails.model.DTO;

import com.fasterxml.jackson.databind.JsonNode;

public class UserConfigurationDTO {
    private boolean travelAlerts;

    public boolean getTravelAlerts() {
        return travelAlerts;
    }

    public void setTravelAlerts(boolean travelAlerts) {
        this.travelAlerts = travelAlerts;
    }


}
