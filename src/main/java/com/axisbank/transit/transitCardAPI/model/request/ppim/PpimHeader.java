package com.axisbank.transit.transitCardAPI.model.request.ppim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PpimHeader {

    @JsonProperty("SessionInfo")
    private PpimSessionInfo ppimSessionInfo;

    public PpimSessionInfo getSessionInfo() {
        return ppimSessionInfo;
    }

    public void setSessionInfo(PpimSessionInfo ppimSessionInfo) {
        this.ppimSessionInfo = ppimSessionInfo;
    }

    @Override
    public String toString() {
        return "PpimHeader{" +
                "ppimSessionInfo=" + ppimSessionInfo +
                '}';
    }
}
