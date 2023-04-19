package com.axisbank.transit.kmrl.model.DTO;

import java.util.HashSet;
import java.util.Set;

public class MetroLineDTO {

    private String displayName;

    private String lineCode;

    private Set<MetroStationDTO> station = new HashSet<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public Set<MetroStationDTO> getStation() {
        return station;
    }

    public void setStation(Set<MetroStationDTO> station) {
        this.station = station;
    }
}
