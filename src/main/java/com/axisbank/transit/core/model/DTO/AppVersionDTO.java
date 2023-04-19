package com.axisbank.transit.core.model.DTO;

public class AppVersionDTO {
    private String androidMinVersion;
    private String iosMinVersion;

    public String getAndroidMinVersion() {
        return androidMinVersion;
    }

    public void setAndroidMinVersion(String androidMinVersion) {
        this.androidMinVersion = androidMinVersion;
    }

    public String getIosMinVersion() {
        return iosMinVersion;
    }

    public void setIosMinVersion(String iosMinVersion) {
        this.iosMinVersion = iosMinVersion;
    }
}
