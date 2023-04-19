package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "bus_fare_ver")
public class BusFareVer extends BaseEntity {

    @Column(name = "fare_id")
    private String fareId;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private BusRoute busRoute;

    @ManyToOne
    @JoinColumn(name="from_station_id")
    private BusStation fromBusStation;

    @ManyToOne
    @JoinColumn(name="to_station_id")
    private BusStation toBusStation;

    @Column(name="fare")
    private double fare;

    @ManyToOne
    @JoinColumn(name = "bus_fare_type_id")
    private BusFareType busFareType;

    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public BusStation getFromBusStation() {
        return fromBusStation;
    }

    public void setFromBusStation(BusStation fromBusStation) {
        this.fromBusStation = fromBusStation;
    }

    public BusStation getToBusStation() {
        return toBusStation;
    }

    public void setToBusStation(BusStation toBusStation) {
        this.toBusStation = toBusStation;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }

    public BusFareType getBusFareType() {
        return busFareType;
    }

    public void setBusFareType(BusFareType busFareType) {
        this.busFareType = busFareType;
    }
}
