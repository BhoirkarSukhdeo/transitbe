package com.axisbank.transit.explore.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "slot")
public class SlotDAO extends BaseEntity {

    @Column(name = "start_date")
    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate endDate;

    @Column(name = "fees")
    private String fees;

    @Column(name = "ticket_type")
    private String ticketType;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @ManyToOne
    @JoinColumn(name = "explore_id")
    private ExploreDAO exploreDAO;

}
