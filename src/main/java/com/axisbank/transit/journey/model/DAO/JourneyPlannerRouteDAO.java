package com.axisbank.transit.journey.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "journey_planner_route")
public class JourneyPlannerRouteDAO extends BaseEntity {

    @Column(name = "journey_planner_id")
    private String journeyPlannerId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "total_duration")
    private long totalDuration;

    @Column(name = "departure_time")
    private Time departureTime;

    @Column(name = "arrival_time")
    private Time arrivalTime ;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @OneToMany(mappedBy = "journeyPlannerRouteDAO", cascade = CascadeType.ALL)
    private List<JourneyModeDetailsDAO> journeyModeDetailsDAOList;
}
