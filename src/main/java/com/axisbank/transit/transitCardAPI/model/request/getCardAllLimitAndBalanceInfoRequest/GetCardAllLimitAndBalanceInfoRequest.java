package com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GetCardAllLimitAndBalanceInfoRequest {

    @JsonProperty("GetCardAllLimitAndBalanceInfo")
    private GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo;


    public GetCardAllLimitAndBalanceInfoRequest(GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo) {
        this.getCardAllLimitAndBalanceInfo=getCardAllLimitAndBalanceInfo;
    }


    public GetCardAllLimitAndBalanceInfoRequest() {

    }
    public GetCardAllLimitAndBalanceInfo getGetCardAllLimitAndBalanceInfo() {
        return getCardAllLimitAndBalanceInfo;
    }

    public void setGetCardAllLimitAndBalanceInfo(GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo) {
        this.getCardAllLimitAndBalanceInfo = getCardAllLimitAndBalanceInfo;
    }

    @Override
    public String toString() {
        return "GetCardAllLimitAndBalanceInfoRequest{" +
                "getCardAllLimitAndBalanceInfo=" + getCardAllLimitAndBalanceInfo +
                '}';
    }
}

