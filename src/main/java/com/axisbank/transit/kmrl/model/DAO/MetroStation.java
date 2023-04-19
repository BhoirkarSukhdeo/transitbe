package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "metro_station")
@Audited
public class MetroStation extends BaseEntity {

    @NotAudited
    @Column(name = "station_id")
    private String stationId;

    @Column(name="display_name")
    private String displayName;

    @Column(name="distance")
    private Double distance;

    @Column(name="station_code_up")
    private String stationCodeUp;

    @Column(name="station_code_dn")
    private String stationCodeDn;

    @Column (name = "latitude")
    private Double latitude;

    @Column (name="longitude")
    private Double longitude;

    @Column(name="geo_id")
    private String geoId;

    @Column(name="station_code")
    private String stationCode;

    @Column(name="sel_station_id")
    private int setStationId;

    @OneToMany(mappedBy = "fromMetroStation")
    private Set<TicketDAO> fromTickets;

    @OneToMany(mappedBy = "toMetroStation")
    private Set<TicketDAO> toTickets;

    @AuditJoinTable
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name = "station_metro_line",
            joinColumns = @JoinColumn(name = "metro_station_id"),
            inverseJoinColumns = @JoinColumn(name = "metro_line_id")
    )
    private Set<MetroLine> metroLine = new HashSet<>();

    @NotAudited
    @OneToMany(mappedBy = "station",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MetroTimeTable> timeTables;

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

    public String getStationCodeUp() {
        return stationCodeUp;
    }

    public void setStationCodeUp(String stationCodeUp) {
        this.stationCodeUp = stationCodeUp;
    }

    public String getStationCodeDn() {
        return stationCodeDn;
    }

    public void setStationCodeDn(String stationCodeDn) {
        this.stationCodeDn = stationCodeDn;
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

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }

    public Set<MetroLine> getMetroLine() {
        return metroLine;
    }

    public void setMetroLine(Set<MetroLine> metroLine) {
        this.metroLine = metroLine;
    }

    public Set<MetroTimeTable> getTimeTables() {
        return timeTables;
    }

    public void setTimeTables(Set<MetroTimeTable> timeTables) {
        this.timeTables = timeTables;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public int getSetStationId() {
        return setStationId;
    }

    public void setSetStationId(int setStationId) {
        this.setStationId = setStationId;
    }

    public Set<TicketDAO> getFromTickets() {
        return fromTickets;
    }

    public void setFromTickets(Set<TicketDAO> fromTickets) {
        this.fromTickets = fromTickets;
    }

    public Set<TicketDAO> getToTickets() {
        return toTickets;
    }

    public void setToTickets(Set<TicketDAO> toTickets) {
        this.toTickets = toTickets;
    }
}
