package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.*;
import java.sql.Time;

@Entity(name = "metro_timetable")
public class MetroTimeTable extends BaseEntity {

    @Column(name = "timetable_id")
    private String timeTableId;

    @Column(name = "sr_num")
    private long srNum;

    @Column(name = "dwelltime")
    private long dwelltime;

    @Column(name = "prev_runtime")
    private long prevRuntime;

    @Column(name = "total_runtime")
    private long totalRuntime;

    @Column(name = "arival_time")
    private Time arivalTime;

    @Column(name = "departure_time")
    private Time departureTime;

    @ManyToOne
    @JoinColumn(name = "metro_station_id")
    private MetroStation station;

    @ManyToOne
    @JoinColumn(name = "metro_trip_id")
    private MetroTrip trip;

    @Column(name="station_name")
    private String stationName;

    @Column(name="metro_line_name")
    private String metroLineName;

    public String getTimeTableId() {
        return timeTableId;
    }

    public void setTimeTableId(String timeTableId) {
        this.timeTableId = timeTableId;
    }

    public long getSrNum() {
        return srNum;
    }

    public void setSrNum(long srNum) {
        this.srNum = srNum;
    }

    public long getDwelltime() {
        return dwelltime;
    }

    public void setDwelltime(long dwelltime) {
        this.dwelltime = dwelltime;
    }

    public long getPrevRuntime() {
        return prevRuntime;
    }

    public void setPrevRuntime(long prevRuntime) {
        this.prevRuntime = prevRuntime;
    }

    public long getTotalRuntime() {
        return totalRuntime;
    }

    public void setTotalRuntime(long totalRuntime) {
        this.totalRuntime = totalRuntime;
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

    public MetroStation getStation() {
        return station;
    }

    public void setStation(MetroStation station) {
        this.station = station;
    }

    public MetroTrip getTrip() {
        return trip;
    }

    public void setTrip(MetroTrip trip) {
        this.trip = trip;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getMetroLineName() {
        return metroLineName;
    }

    public void setMetroLineName(String metroLineName) {
        this.metroLineName = metroLineName;
    }
}

