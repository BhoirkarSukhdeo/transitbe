package com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubHeader {

    @JsonProperty("requestUUID")
    public String requestUUID;

    @JsonProperty("serviceRequestId")
    public String serviceRequestId;

    @JsonProperty("serviceRequestVersion")
    public String serviceRequestVersion;

    @JsonProperty("channelId")
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
}
