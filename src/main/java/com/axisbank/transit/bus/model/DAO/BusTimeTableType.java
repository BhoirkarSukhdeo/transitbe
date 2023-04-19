package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.List;

@Audited
@Entity(name = "bus_timetable_type")
public class BusTimeTableType extends BaseEntity {

    @NotAudited
    @Column(name = "bs_timetable_type_id")
    private String busTimetableId;

    @Column(name = "timetable_name")
    String timeTableName;

    @Column(name = "active_days")
    String activeDays;

    @Column(name = "current_status")
    String currentStatus;

    @NotAudited
    @OneToMany(mappedBy = "timeTableType",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BusTimeTableVer> timeTables;

    public String getBusTimetableId() {
        return busTimetableId;
    }

    public void setBusTimetableId(String mtTimetableId) {
        this.busTimetableId = mtTimetableId;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<BusTimeTableVer> getTimeTables() {
        return timeTables;
    }

    public void setTimeTables(List<BusTimeTableVer> timeTables) {
        this.timeTables = timeTables;
    }
}
