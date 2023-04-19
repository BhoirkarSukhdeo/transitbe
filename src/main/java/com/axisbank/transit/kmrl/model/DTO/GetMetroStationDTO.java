package com.axisbank.transit.kmrl.model.DTO;

public class GetMetroStationDTO extends MetroStationDTO {

    private boolean isSource;

    public boolean isSource() {
        return isSource;
    }

    public void setIsSource(boolean source) {
        isSource = source;
    }
}
