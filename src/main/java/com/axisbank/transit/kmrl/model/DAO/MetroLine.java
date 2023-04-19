package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "metro_line")
@Audited
public class MetroLine extends BaseEntity {

    @NotAudited
    @Column(name = "line_id")
    private String lineId;

    @Column(name="display_name")
    private String displayName;

    @Column(name="line_code")
    private String lineCode;

    @ManyToMany(targetEntity = MetroStation.class, mappedBy = "metroLine")
    private Set<MetroStation> stations = new HashSet<>();

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public Set<MetroStation> getStations() {
        return stations;
    }

    public void setStations(Set<MetroStation> stations) {
        this.stations = stations;
    }
}

