package com.axisbank.transit.transitCardAPI.model.request.updateCustomer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdressInfo {

    @JsonProperty("AdressIdx")
    private String adressIdx;
    @JsonProperty("AdressType")
    private String adressType;
    @JsonProperty("Address1")
    private String address1;
    @JsonProperty("Address2")
    private String address2;
    @JsonProperty("Address3")
    private String address3;
    @JsonProperty("AddressCity")
    private String addressCity;
    @JsonProperty("AddressCityCode")
    private String addressCityCode;
    @JsonProperty("AddressTown")
    private String addressTown;
    @JsonProperty("AddressTownCode")
    private String addressTownCode;
    @JsonProperty("AddressCountry")
    private String addressCountry;
    @JsonProperty("AddressZipCode")
    private String addressZipCode;
    @JsonProperty("AddressStateCode")
    private String addressStateCode;
    @JsonProperty("AddressState")
    private String addressState;

    public String getAdressIdx() {
        return adressIdx;
    }

    public void setAdressIdx(String adressIdx) {
        this.adressIdx = adressIdx;
    }

    public String getAdressType() {
        return adressType;
    }

    public void setAdressType(String adressType) {
        this.adressType = adressType;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressCityCode() {
        return addressCityCode;
    }

    public void setAddressCityCode(String addressCityCode) {
        this.addressCityCode = addressCityCode;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressTownCode() {
        return addressTownCode;
    }

    public void setAddressTownCode(String addressTownCode) {
        this.addressTownCode = addressTownCode;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public String getAddressStateCode() {
        return addressStateCode;
    }

    public void setAddressStateCode(String addressStateCode) {
        this.addressStateCode = addressStateCode;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    @Override
    public String toString() {
        return "AdressInfo{" +
                "adressIdx='" + adressIdx + '\'' +
                ", adressType='" + adressType + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", addressCity='" + addressCity + '\'' +
                ", addressCityCode='" + addressCityCode + '\'' +
                ", addressTown='" + addressTown + '\'' +
                ", addressTownCode='" + addressTownCode + '\'' +
                ", addressCountry='" + addressCountry + '\'' +
                ", addressZipCode='" + addressZipCode + '\'' +
                ", addressStateCode='" + addressStateCode + '\'' +
                ", addressState='" + addressState + '\'' +
                '}';
    }
}
