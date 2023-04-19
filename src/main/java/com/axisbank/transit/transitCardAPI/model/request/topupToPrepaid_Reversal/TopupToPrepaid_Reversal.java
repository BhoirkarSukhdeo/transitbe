package com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopupToPrepaid_Reversal {

    @JsonProperty("TopupToPrepaid_Reversal")
    private TopupToPrepaidReversal topupToPrepaid_Reversal;

    public TopupToPrepaidReversal getTopupToPrepaid_Reversal() {
        return topupToPrepaid_Reversal;
    }

    public void setTopupToPrepaid_Reversal(TopupToPrepaidReversal topupToPrepaid_Reversal) {
        this.topupToPrepaid_Reversal = topupToPrepaid_Reversal;
    }

    @Override
    public String toString() {
        return "TopupToPrepaid_Reversal{" +
                "topupToPrepaid_Reversal=" + topupToPrepaid_Reversal +
                '}';
    }
}

