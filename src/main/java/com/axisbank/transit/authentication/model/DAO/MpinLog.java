package com.axisbank.transit.authentication.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

@Entity(name = "mpin_log")
public class MpinLog extends BaseEntity {

    @Size(min = 6)
    @Column(name = "mpin")
    private String mpin;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }
}
