package com.axisbank.transit.bus.model.DTO;

public class BusSrcDestRouteDTO {
    String sourceStation;
    String destinationStation;
    String routeCode;
    int srNum;

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public int getSrNum() {
        return srNum;
    }

    public void setSrNum(int srNum) {
        this.srNum = srNum;
    }

    @Override
    public String toString() {
        return "BusSrcDestRouteDTO{" +
                "sourceStation='" + sourceStation + '\'' +
                ", destinationStation='" + destinationStation + '\'' +
                ", routeCode='" + routeCode + '\'' +
                ", srNum=" + srNum +
                '}';
    }
}
