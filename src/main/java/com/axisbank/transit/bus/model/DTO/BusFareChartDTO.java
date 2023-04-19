package com.axisbank.transit.bus.model.DTO;

import java.util.List;

public class BusFareChartDTO {
    private String routeName;
    private List<StationFareListDTO> stationList;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<StationFareListDTO> getStationList() {
        return stationList;
    }

    public void setStationList(List<StationFareListDTO> stationList) {
        this.stationList = stationList;
    }
}
