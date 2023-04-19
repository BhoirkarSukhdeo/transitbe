package com.axisbank.transit.bus.model.DTO;

import java.util.List;

public class StationFareListDTO {
    private String primaryStationName;
    private List<StationFareDTO> fareChart;

    public String getPrimaryStationName() {
        return primaryStationName;
    }

    public void setPrimaryStationName(String primaryStationName) {
        this.primaryStationName = primaryStationName;
    }

    public List<StationFareDTO> getFareChart() {
        return fareChart;
    }

    public void setFareChart(List<StationFareDTO> fareChart) {
        this.fareChart = fareChart;
    }
}
