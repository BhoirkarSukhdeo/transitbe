package com.axisbank.transit.authentication.model.DAO;

import com.axisbank.transit.core.model.DAO.BaseEntity;

import javax.persistence.*;

@Entity(name = "login_log")
public class LoginLog extends BaseEntity {
    @Column(name = "login_log_ref_id")
    private String loginLogRefId;
    @Column(name = "login_type")
    private String loginType;
    @Column(name = "login_status")
    private String loginStatus;
    @Column(name = "request_type")
    private String requestType;
    @Lob
    @Column(name = "login_comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    public LoginLog(String loginLogRefId, String loginType, String loginStatus, AuthenticationDAO authenticationDAO,
                    String requestType) {
        this.loginLogRefId = loginLogRefId;
        this.loginType = loginType;
        this.loginStatus = loginStatus;
        this.authenticationDAO = authenticationDAO;
        this.requestType = requestType;
    }

    public LoginLog() {
    }

    public String getLoginLogRefId() {
        return loginLogRefId;
    }

    public void setLoginLogRefId(String loginLogRefId) {
        this.loginLogRefId = loginLogRefId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
