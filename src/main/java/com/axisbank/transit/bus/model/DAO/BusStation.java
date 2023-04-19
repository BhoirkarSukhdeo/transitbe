package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity(name = "bus_station")
public class BusStation extends BaseEntity {

    @NotAudited
    @Column(name = "station_id")
    private String stationId;

    @Column(name="display_name")
    private String displayName;

    @Column (name = "latitude")
    private Double latitude;

    @Column (name="longitude")
    private Double longitude;

    @Column(name="station_code", unique = true)
    private String stationCode;

    @NotAudited
    @OneToMany(mappedBy = "fromBusStation")
    private Set<BusFare> fromFares;

    @NotAudited
    @OneToMany(mappedBy = "toBusStation")
    private Set<BusFare> toFares;

    @NotAudited
    @OneToMany(mappedBy = "busStation", cascade = CascadeType.ALL)
    private Set<BusTimeTable> busTimeTableSet;

    @AuditJoinTable
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "bus_station_route",
            joinColumns = {
                    @JoinColumn(name = "station_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "route_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<BusRoute> routeSet = new HashSet<>();

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public Set<BusRoute> getRouteSet() {
        return routeSet;
    }

    public void setRouteSet(Set<BusRoute> routeSet) {
        this.routeSet = routeSet;
    }

    public Set<BusFare> getFromFares() {
        return fromFares;
    }

    public void setFromFares(Set<BusFare> fromFares) {
        this.fromFares = fromFares;
    }

    public Set<BusFare> getToFares() {
        return toFares;
    }

    public void setToFares(Set<BusFare> toFares) {
        this.toFares = toFares;
    }

    public Set<BusTimeTable> getBusTimeTableSet() {
        return busTimeTableSet;
    }

    public void setBusTimeTableSet(Set<BusTimeTable> busTimeTableSet) {
        this.busTimeTableSet = busTimeTableSet;
    }
}
