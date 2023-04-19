package com.axisbank.transit.bus.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.List;

@Audited
@Entity(name = "bus_fare_type")
public class BusFareType extends BaseEntity {

    @NotAudited
    @Column(name = "bs_fare_type_id")
    private String busFareTypeId;

    @Column(name = "bus_fare_name")
    String busFareName;

    @Column(name = "active_days")
    String activeDays;

    @Column(name = "current_status")
    String currentStatus;

    @NotAudited
    @OneToMany(mappedBy = "busFareType",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BusFareVer> fares;

    public String getBusFareTypeId() {
        return busFareTypeId;
    }

    public void setBusFareTypeId(String busFareTypeId) {
        this.busFareTypeId = busFareTypeId;
    }

    public String getBusFareName() {
        return busFareName;
    }

    public void setBusFareName(String busFareName) {
        this.busFareName = busFareName;
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

    public List<BusFareVer> getFares() {
        return fares;
    }

    public void setFares(List<BusFareVer> fares) {
        this.fares = fares;
    }
}
