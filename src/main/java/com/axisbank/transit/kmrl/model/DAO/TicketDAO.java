package com.axisbank.transit.kmrl.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

@Entity(name = "ticket")
@Audited
public class TicketDAO extends BaseEntity {

    @NotAudited
    @Column(name = "ticket_ref_id")
    private String ticketRefId;

    @Column(name="ticket_gu_id")
    private String ticketGUID;

    @Column(name="ticket_no")
    private String ticketNo;

    @Column(name="ticket_fare")
    private double ticketFare;

    @Column(name="transport_mode")
    private String transportMode;

    @Column(name="travellers")
    private short travellers;

    @Column(name="ticket_type")
    private String ticketType;

    @Column(name="description")
    private String description;

    @Column(name="ticket_transaction_id")
    private String ticketTransactionId;

    @Column(name="ticket_status")
    private String ticketStatus;

    @Column(name = "secondary_ticket_status")
    private String secondaryTicketStatus;

    @Column(name = "journey_date")
    private String journeyDate;

    @NotAudited
    @ManyToOne
    @JoinColumn(name="from_station_id")
    private MetroStation fromMetroStation;

    @NotAudited
    @ManyToOne
    @JoinColumn(name="to_station_id")
    private MetroStation toMetroStation;

    @NotAudited
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @NotAudited
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionDAO transactionDAO;


    public String getTicketRefId() {
        return ticketRefId;
    }

    public void setTicketRefId(String ticketRefId) {
        this.ticketRefId = ticketRefId;
    }

    public String getTicketGUID() {
        return ticketGUID;
    }

    public void setTicketGUID(String ticketGUID) {
        this.ticketGUID = ticketGUID;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public double getTicketFare() {
        return ticketFare;
    }

    public void setTicketFare(double ticketFare) {
        this.ticketFare = ticketFare;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public short getTravellers() {
        return travellers;
    }

    public void setTravellers(short travellers) {
        this.travellers = travellers;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketTransactionId() {
        return ticketTransactionId;
    }

    public void setTicketTransactionId(String ticketTransactionId) {
        this.ticketTransactionId = ticketTransactionId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public MetroStation getFromMetroStation() {
        return fromMetroStation;
    }

    public void setFromMetroStation(MetroStation fromMetroStation) {
        this.fromMetroStation = fromMetroStation;
    }

    public MetroStation getToMetroStation() {
        return toMetroStation;
    }

    public void setToMetroStation(MetroStation toMetroStation) {
        this.toMetroStation = toMetroStation;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getSecondaryTicketStatus() {
        return secondaryTicketStatus;
    }

    public void setSecondaryTicketStatus(String secondaryTicketStatus) {
        this.secondaryTicketStatus = secondaryTicketStatus;
    }
}
