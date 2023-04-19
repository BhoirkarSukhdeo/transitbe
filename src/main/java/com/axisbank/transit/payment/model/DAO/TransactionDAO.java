package com.axisbank.transit.payment.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.BaseEntity;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "transaction")
@Audited
public class TransactionDAO extends BaseEntity {
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "payment_service_provider")
    private String paymentServiceProvider;

    @Column(name = "psp_ref_id")
    private String pspRefId;

    @Column(name="psp_txn_id")
    private String pspTxnId;

    @Column(name = "psp_status")
    private String pspStatus;

    @Column(name = "psp_payment_method")
    private String pspPaymentMethod;

    @Column(name = "psp_payment_method_type")
    private String pspPaymentMethodType;

    @Column(name = "service_provider")
    private String serviceProvider;

    @Column(name = "sp_ref_id")
    private String spRefId;

    @Column(name = "sp_txn_id")
    private String spTxnId;

    @Column(name = "sp_status")
    private String spStatus;

    @Column(name = "amount")
    private double amount;

    @Column(name = "final_txn_status")
    private String finalTxnStatus;

    @Column(name = "txn_type")
    private String txnType;

    @Column(name="mid")
    private String merchantId;

    @CreatedDate
    @Column(name = "txn_initiated_on", nullable = false, updatable = false)
    private Date txnInitiatedOn;

    @Column(name = "txn_completed_on")
    private Date txnCompletedOn;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="linked_txn")
    private TransactionDAO linkedTxn;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @OneToMany(mappedBy="linkedTxn", cascade = CascadeType.ALL)
    private Set<TransactionDAO> allLinkedTxns = new HashSet<>();

    @OneToOne(mappedBy = "transactionDAO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TicketDAO ticketDAO;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentServiceProvider() {
        return paymentServiceProvider;
    }

    public void setPaymentServiceProvider(String paymentServiceProvider) {
        this.paymentServiceProvider = paymentServiceProvider;
    }

    public String getPspRefId() {
        return pspRefId;
    }

    public void setPspRefId(String pspRefId) {
        this.pspRefId = pspRefId;
    }

    public String getPspTxnId() {
        return pspTxnId;
    }

    public void setPspTxnId(String pspTxnId) {
        this.pspTxnId = pspTxnId;
    }

    public String getPspStatus() {
        return pspStatus;
    }

    public void setPspStatus(String pspStatus) {
        this.pspStatus = pspStatus;
    }

    public String getPspPaymentMethod() {
        return pspPaymentMethod;
    }

    public void setPspPaymentMethod(String pspPaymentMethod) {
        this.pspPaymentMethod = pspPaymentMethod;
    }

    public String getPspPaymentMethodType() {
        return pspPaymentMethodType;
    }

    public void setPspPaymentMethodType(String pspPaymentMethodType) {
        this.pspPaymentMethodType = pspPaymentMethodType;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getSpRefId() {
        return spRefId;
    }

    public void setSpRefId(String spRefId) {
        this.spRefId = spRefId;
    }

    public String getSpTxnId() {
        return spTxnId;
    }

    public void setSpTxnId(String spTxnId) {
        this.spTxnId = spTxnId;
    }

    public String getSpStatus() {
        return spStatus;
    }

    public void setSpStatus(String spStatus) {
        this.spStatus = spStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFinalTxnStatus() {
        return finalTxnStatus;
    }

    public void setFinalTxnStatus(String finalTxnStatus) {
        this.finalTxnStatus = finalTxnStatus;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public Date getTxnInitiatedOn() {
        return txnInitiatedOn;
    }

    public void setTxnInitiatedOn(Date txnInitiatedOn) {
        this.txnInitiatedOn = txnInitiatedOn;
    }

    public Date getTxnCompletedOn() {
        return txnCompletedOn;
    }

    public void setTxnCompletedOn(Date txnCompletedOn) {
        this.txnCompletedOn = txnCompletedOn;
    }

    public TransactionDAO getLinkedTxn() {
        return linkedTxn;
    }

    public void setLinkedTxn(TransactionDAO linkedTxn) {
        this.linkedTxn = linkedTxn;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public Set<TransactionDAO> getAllLinkedTxns() {
        return allLinkedTxns;
    }

    public void setAllLinkedTxns(Set<TransactionDAO> allLinkedTxns) {
        this.allLinkedTxns = allLinkedTxns;
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }

    public void setTicketDAO(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
