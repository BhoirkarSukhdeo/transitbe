package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthSessionInfo {

    @JsonProperty("AuthUserID")
    public String authUserID;
    @JsonProperty("AuthPassword")
    public String authPassword;
    @JsonProperty("UserCode")
    public String userCode;
    @JsonProperty("Language")
    public String language;
    @JsonProperty("MbrId")
    public String mbrId;

    public String getAuthUserID() {
        return authUserID;
    }

    public void setAuthUserID(String authUserID) {
        this.authUserID = authUserID;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMbrId() {
        return mbrId;
    }

    public void setMbrId(String mbrId) {
        this.mbrId = mbrId;
    }

    @Override
    public String toString() {
        return "AuthSessionInfo{" +
                "authUserID='" + authUserID + '\'' +
                ", authPassword='" + authPassword + '\'' +
                ", userCode=" + userCode +
                ", language='" + language + '\'' +
                ", mbrId='" + mbrId + '\'' +
                '}';
    }
}
