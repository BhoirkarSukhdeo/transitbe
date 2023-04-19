package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity(name = "bus_route")
public class BusRoute extends BaseEntity {

    @NotAudited
    @Column(name = "route_id")
    private String routeId;

    @Column(name="route_name")
    private String routeName;

    @Column (name = "route_name_up")
    private String routeNameUp;

    @Column (name="route_name_down")
    private String routeNameDown;

    @Column(name="route_code", unique = true)
    private String routeCode;

    @Column (name = "association")
    private String association;

    @Column (name="vehicle_number")
    private String vehicleNumber;

    @Column(name="bus_type")
    private String busType;

    @ManyToMany(mappedBy = "routeSet", fetch = FetchType.LAZY)
    private Set<BusStation> busStationSet = new HashSet<>();

    @NotAudited
    @OneToMany(mappedBy = "busRoute", cascade = CascadeType.ALL)
    private Set<BusFare> busFareSet;

    @NotAudited
    @OneToMany(mappedBy = "busRoute", cascade = CascadeType.ALL)
    private Set<BusTimeTable> busTimeTableSet;

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

    public Set<BusStation> getBusStationSet() {
        return busStationSet;
    }

    public void setBusStationSet(Set<BusStation> busStationSet) {
        this.busStationSet = busStationSet;
    }

    public Set<BusFare> getBusFareSet() {
        return busFareSet;
    }

    public void setBusFareSet(Set<BusFare> busFareSet) {
        this.busFareSet = busFareSet;
    }

    public Set<BusTimeTable> getBusTimeTableSet() {
        return busTimeTableSet;
    }

    public void setBusTimeTableSet(Set<BusTimeTable> busTimeTableSet) {
        this.busTimeTableSet = busTimeTableSet;
    }
}
