package com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCardAllLimitAndBalanceInfo {
    @JsonProperty("CardNo")
    private String cardNo;
    @JsonProperty("LimitType")
    private String limitType="";


    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    @Override
    public String toString() {
        return "GetCardAllLimitAndBalanceInfo{" +
                "cardNo='" + cardNo + '\'' +
                ", limitType='" + limitType + '\'' +
                '}';
    }
}
