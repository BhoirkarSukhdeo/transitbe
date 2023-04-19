package com.axisbank.transit.transitCardAPI.model.request;

public class GetCityList {

    private String countryCode;
    private String stateCode;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String toString() {
        return "GetCityList{" +
                "countryCode='" + countryCode + '\'' +
                ", stateCode='" + stateCode + '\'' +
                '}';
    }
}
