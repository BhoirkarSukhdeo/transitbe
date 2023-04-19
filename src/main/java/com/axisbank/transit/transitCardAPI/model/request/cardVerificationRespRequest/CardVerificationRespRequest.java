package com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardVerificationRespRequest {

    @JsonProperty("CardVerification")
    private CardVerificationResp cardVerificationResp;

    public CardVerificationResp getCardVerificationResp() {
        return cardVerificationResp;
    }

    public void setCardVerificationResp(CardVerificationResp cardVerificationResp) {
        this.cardVerificationResp = cardVerificationResp;
    }

    @Override
    public String toString() {
        return "CardVerificationRespReq{" +
                "cardVerificationResp=" + cardVerificationResp +
                '}';
    }
}

