package com.axisbank.transit.core.model.response;

public class NotificationDetailDTO {
    private String title;
    private String subTitle;
    private String description;
    private String bannerUrl;

    public NotificationDetailDTO() {
    }

    public NotificationDetailDTO(String title, String subTitle, String description, String bannerUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.bannerUrl = bannerUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}
