package com.axisbank.transit.bus.model.DTO;

import com.axisbank.transit.bus.model.DAO.BusStationMappedDTO;

import java.util.List;

public class BusRouteAdminDTO {

    private String routeId;
    private String routeName;
    private String routeNameUp;
    private String routeNameDown;
    private String routeCode;
    private String association;
    private String vehicleNumber;
    private String busType;
    private List<BusStationMappedDTO> stations;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteNameUp() {
        return routeNameUp;
    }

    public void setRouteNameUp(String routeNameUp) {
        this.routeNameUp = routeNameUp;
    }

    public String getRouteNameDown() {
        return routeNameDown;
    }

    public void setRouteNameDown(String routeNameDown) {
        this.routeNameDown = routeNameDown;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public List<BusStationMappedDTO> getStations() {
        return stations;
    }

    public void setStations(List<BusStationMappedDTO> stations) {
        this.stations = stations;
    }
}
