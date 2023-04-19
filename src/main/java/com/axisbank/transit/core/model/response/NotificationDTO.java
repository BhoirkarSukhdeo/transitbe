package com.axisbank.transit.core.model.response;

import java.util.Date;

public class NotificationDTO {
    private String id;
    private String typeId;
    private String type;
    private String title;
    private String subTitle;
    private String status;
    private boolean seen;
    private Date dateTime;

    public NotificationDTO() {
    }

    public NotificationDTO(String id, String typeId, String type, String title, String subTitle, String status, boolean seen, Date dateTime) {
        this.id = id;
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.status = status;
        this.seen = seen;
        this.dateTime = dateTime;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateTime() {
        return dateTime;
    }
}
