package com.axisbank.transit.authentication.model.request;

public class RefreshTokenWithMpinRequest {
    private String refreshToken;
    private String mpin;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
}
