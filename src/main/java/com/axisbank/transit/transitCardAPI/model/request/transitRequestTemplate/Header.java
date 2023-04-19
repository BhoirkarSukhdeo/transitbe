package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

    @JsonProperty("AuthSessionInfo")
    public AuthSessionInfo authSessionInfo;
    public SubHeader subHeader;

    public AuthSessionInfo getAuthSessionInfo() {
        return authSessionInfo;
    }

    public void setAuthSessionInfo(AuthSessionInfo authSessionInfo) {
        this.authSessionInfo = authSessionInfo;
    }

    public SubHeader getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(SubHeader subHeader) {
        this.subHeader = subHeader;
    }

    @Override
    public String toString() {
        return "Header{" +
                "authSessionInfo=" + authSessionInfo +
                ", subHeader=" + subHeader +
                '}';
    }
}
