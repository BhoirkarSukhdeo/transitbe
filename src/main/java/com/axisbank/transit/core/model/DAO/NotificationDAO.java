package com.axisbank.transit.core.model.DAO;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "notification")
public class NotificationDAO extends BaseEntity {

    @Column(name= "notification_ref_id")
    private String notificationRefId;

    @Column(name="notification_title")
    private String title;

    @Column(name="sub_title")
    private String subTitle;

    @Column(name="notification_body", length = 1000)
    private String body;

    @Column(name="action")
    private String action;

    @Column(name="type")
    private String type;

    @Column(name="type_id")
    private String typeId;

    @Column(name="status")
    private String status;

    @Column(name="seen")
    private boolean seen;

    @Column(name="banner_url")
    private String bannerUrl;

    @CreatedDate
    @Column(name="creation_date_time")
    private Date creationDateTime;

    @ManyToOne
    @JoinColumn(name = "authentication_id")
    private AuthenticationDAO authenticationDAO;

    @Column(name = "expiry_date")
    private Date expiryDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getNotificationRefId() {
        return notificationRefId;
    }

    public void setNotificationRefId(String notificationRefId) {
        this.notificationRefId = notificationRefId;
    }

    public AuthenticationDAO getAuthenticationDAO() {
        return authenticationDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
