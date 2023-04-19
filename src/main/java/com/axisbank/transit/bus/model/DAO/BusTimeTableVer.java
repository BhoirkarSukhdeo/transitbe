package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Time;

@Entity(name = "bus_timetable_ver")
public class BusTimeTableVer extends BaseEntity {

    @Column(name = "timetable_id")
    private String timeTableId;

    @Column(name = "arival_time")
    private Time arivalTime;

    @Column(name = "departure_time")
    private Time departureTime;

    @Column(name = "trip_number")
    private int tripNumber;

    @Column(name = "route_type")
    private String routeType;

    @Column(name = "sr_num")
    private int srNum;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private BusRoute busRoute;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private BusStation busStation;

    @ManyToOne
    @JoinColumn(name = "bus_timetable_type_id")
    private BusTimeTableType timeTableType;

    public String getTimeTableId() {
        return timeTableId;
    }

    public void setTimeTableId(String timeTableId) {
        this.timeTableId = timeTableId;
    }

    public Time getArivalTime() {
        return arivalTime;
    }

    public void setArivalTime(Time arivalTime) {
        this.arivalTime = arivalTime;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public int getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(int tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }

    public BusStation getBusStation() {
        return busStation;
    }

    public void setBusStation(BusStation busStation) {
        this.busStation = busStation;
    }

    public int getSrNum() {
        return srNum;
    }

    public void setSrNum(int srNum) {
        this.srNum = srNum;
    }

    public BusTimeTableType getTimeTableType() {
        return timeTableType;
    }

    public void setTimeTableType(BusTimeTableType timeTableType) {
        this.timeTableType = timeTableType;
    }
}
