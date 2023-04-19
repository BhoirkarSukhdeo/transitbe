package com.axisbank.transit.bus.model.DTO;

public class GetBusStationDTO extends BusStationDTO {

    private boolean isSource;

    public boolean isSource() {
        return isSource;
    }

    public void setIsSource(boolean source) {
        isSource = source;
    }
}
