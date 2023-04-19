package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity(name = "metro_trip_ver")
public class MetroTripVer extends BaseEntity {

    @Column(name = "mt_trip_id")
    private String MtTripId;

    @Column(name="trip_id")
    private String tripId;

    @Column(name="trip_number")
    private String tripNumber;

    @Column(name = "next_number")
    private String nextNumber;

    @Column(name = "prev_number")
    private String prevNumber;

    @Column(name="direction")
    private String direction;

    @Column(name="service_id")
    private String serviceId;

    @Column(name="total_distance")
    private String totalDistance;

    @Column(name = "start_time")
    private Time startTime;

    @OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MetroTimeTableVer> timeTables;

    @ManyToOne
    @JoinColumn(name = "metro_timetable_type_id")
    private MetroTimeTableType timeTableType;

    public String getMtTripId() {
        return MtTripId;
    }

    public void setMtTripId(String mtTripId) {
        MtTripId = mtTripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(String nextNumber) {
        this.nextNumber = nextNumber;
    }

    public String getPrevNumber() {
        return prevNumber;
    }

    public void setPrevNumber(String prevNumber) {
        this.prevNumber = prevNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalTistance) {
        this.totalDistance = totalTistance;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public List<MetroTimeTableVer> getTimeTables() {
        return timeTables;
    }

    public void setTimeTables(List<MetroTimeTableVer> timeTables) {
        this.timeTables = timeTables;
    }

    public MetroTimeTableType getTimeTableType() {
        return timeTableType;
    }

    public void setTimeTableType(MetroTimeTableType timeTableType) {
        this.timeTableType = timeTableType;
    }
}
