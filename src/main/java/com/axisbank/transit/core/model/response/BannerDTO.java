package com.axisbank.transit.core.model.response;

public class BannerDTO {
    private String url;
    private String exploreId;

    public BannerDTO() {
    }

    public BannerDTO(String url, String exploreId) {
        this.url = url;
        this.exploreId = exploreId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExploreId() {
        return exploreId;
    }

    public void setExploreId(String exploreId) {
        this.exploreId = exploreId;
    }
}
