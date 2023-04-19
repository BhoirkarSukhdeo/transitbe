package com.axisbank.transit.transitCardAPI.model.request.matchCardAndNewCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdressList {

    @JsonProperty("AdressInfo")
    private AdressInfo adressInfo;

    public AdressInfo getAdressInfo() {
        return adressInfo;
    }

    public void setAdressInfo(AdressInfo adressInfo) {
        this.adressInfo = adressInfo;
    }

    @Override
    public String toString() {
        return "AdressList{" +
                "adressInfo=" + adressInfo +
                '}';
    }
}
