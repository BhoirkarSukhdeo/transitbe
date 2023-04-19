package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubHeader {

    @JsonProperty("RequestUUID")
    public String requestUUID;

    @JsonProperty("ServiceRequestId")
    public String serviceRequestId;

    @JsonProperty("ServiceRequestVersion")
    public String serviceRequestVersion;

    @JsonProperty("Checksum")
    public String checksum;

    @JsonProperty("ChannelId")
    public String channelId;

    public String getRequestUUID() {
        return requestUUID;
    }

    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getServiceRequestVersion() {
        return serviceRequestVersion;
    }

    public void setServiceRequestVersion(String serviceRequestVersion) {
        this.serviceRequestVersion = serviceRequestVersion;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
