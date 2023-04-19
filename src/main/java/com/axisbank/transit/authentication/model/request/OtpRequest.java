package com.axisbank.transit.authentication.model.request;

import com.axisbank.transit.authentication.constants.GetOtpType;

public class OtpRequest {

    private String mobile;
    private GetOtpType getOtpType;

    public GetOtpType getGetOtpType() {
        return getOtpType;
    }

    public void setGetOtpType(GetOtpType getOtpType) {
        this.getOtpType = getOtpType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
