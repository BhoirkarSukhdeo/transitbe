package com.axisbank.transit.kmrl.model.DTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public class MetroLineAdminDTO {
    private String lineId;
    @NotNull
    private String displayName;
    private String lineCode;
    private List<MetroStationMappedDTO> stations;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

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

    public List<MetroStationMappedDTO> getStations() {
        return stations;
    }

    public void setStations(List<MetroStationMappedDTO> stations) {
        this.stations = stations;
    }
}
