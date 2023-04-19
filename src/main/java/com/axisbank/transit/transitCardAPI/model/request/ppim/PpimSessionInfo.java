package com.axisbank.transit.transitCardAPI.model.request.ppim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PpimSessionInfo {

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("ChannelId")
    private String channelId;

    public PpimSessionInfo(String userName, String password, String channelId) {
        this.userName=userName;
        this.password=password;
        this.channelId=channelId;
    }

    public PpimSessionInfo() {

    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "PpimSessionInfo{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
