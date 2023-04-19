package com.axisbank.transit.journey.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "journey_mode_details")
public class JourneyModeDetailsDAO extends BaseEntity {

    @Column(name = "distance")
    private Double distance;

    @Column(name = "time")
    private Time time;

    @Column(name = "type")
    private String type;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "route")
    private String route;

    @Column(name = "travel_time")
    private Double travelTime;

    @Column(name = "fare")
    private Double fare;

    @Column(name = "intermediate_stops")
    private String intermediateStops;

    @Column(name = "no_of_intermediate_stops")
    private int noOfIntermediateStops;

    @Column(name = "source_latitude")
    private Double sourceLatitude;

    @Column(name = "source_longitude")
    private Double sourceLongitude;

    @Column(name = "destination_latitude")
    private Double destinationLatitude;

    @Column(name = "destination_longitude")
    private Double destinationLongitude;

    @Column(name = "estimated_arrival_time")
    private Time estimatedArrivalTime;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "destination_id")
    private String destinationId;

    @Column(name = "is_ticket_booked")
    private Boolean isTicketBooked = false;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "is_booking_allowed")
    private Boolean isBookingAllowed = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "journey_planner_route_id")
    private JourneyPlannerRouteDAO journeyPlannerRouteDAO;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public int getNoOfIntermediateStops() {
        return noOfIntermediateStops;
    }

    public void setNoOfIntermediateStops(int noOfIntermediateStops) {
        this.noOfIntermediateStops = noOfIntermediateStops;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(Double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public Time getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(Time estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public JourneyPlannerRouteDAO getJourneyPlannerRouteDAO() {
        return journeyPlannerRouteDAO;
    }

    public void setJourneyPlannerRouteDAO(JourneyPlannerRouteDAO journeyPlannerRouteDAO) {
        this.journeyPlannerRouteDAO = journeyPlannerRouteDAO;
    }

    public List<String> getIntermediateStops() {
        if (intermediateStops != null && !intermediateStops.isEmpty()) {
            return Arrays.asList(intermediateStops.split(","));
        }
        return null;
    }

    public void setIntermediateStops(List<String> intermediateStops) {
        if (intermediateStops != null && !intermediateStops.isEmpty() ) {
            this.intermediateStops = String.join(",", intermediateStops);
        } else {
            this.intermediateStops = null;
        }

    }

    public Boolean getTicketBooked() {
        return isTicketBooked;
    }

    public void setTicketBooked(Boolean ticketBooked) {
        isTicketBooked = ticketBooked;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Boolean getBookingAllowed() {
        return isBookingAllowed;
    }

    public void setBookingAllowed(Boolean bookingAllowed) {
        isBookingAllowed = bookingAllowed;
    }
}
