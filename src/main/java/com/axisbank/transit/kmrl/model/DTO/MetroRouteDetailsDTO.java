package com.axisbank.transit.kmrl.model.DTO;

import java.util.List;

public class MetroRouteDetailsDTO {
    private MetroTimetableStationDTO source;
    private MetroTimetableStationDTO destination;
    private List<MetroTimetableStationDTO> intermediateStations;

    public MetroTimetableStationDTO getSource() {
        return source;
    }

    public void setSource(MetroTimetableStationDTO source) {
        this.source = source;
    }

    public MetroTimetableStationDTO getDestination() {
        return destination;
    }

    public void setDestination(MetroTimetableStationDTO destination) {
        this.destination = destination;
    }

    public List<MetroTimetableStationDTO> getIntermediateStations() {
        return intermediateStations;
    }

    public void setIntermediateStations(List<MetroTimetableStationDTO> intermediateStations) {
        this.intermediateStations = intermediateStations;
    }
}
