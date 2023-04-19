package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.List;

@Entity(name = "metro_timetable_type")
@Audited
public class MetroTimeTableType extends BaseEntity {

    @NotAudited
    @Column(name = "mt_timetable_type_id")
    private String mtTimetableId;

    @Column(name = "timetable_name")
    String timeTableName;

    @Column(name = "active_days")
    String activeDays;

    @Column(name = "current_status")
    String currentStatus;

    @NotAudited
    @OneToMany(mappedBy = "timeTableType",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MetroTripVer> trips;

    public String getMtTimetableId() {
        return mtTimetableId;
    }

    public void setMtTimetableId(String mtTimetableId) {
        this.mtTimetableId = mtTimetableId;
    }

    public String getTimeTableName() {
        return timeTableName;
    }

    public void setTimeTableName(String timeTableName) {
        this.timeTableName = timeTableName;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public List<MetroTripVer> getTrips() {
        return trips;
    }

    public void setTrips(List<MetroTripVer> trips) {
        this.trips = trips;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
