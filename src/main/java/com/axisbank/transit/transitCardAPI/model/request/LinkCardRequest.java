package com.axisbank.transit.transitCardAPI.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class LinkCardRequest {

    private String mobileNo;
    private String lastFourDigitCardNo;
    private String name;
    private String lastName="";

    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDate dob;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getLastFourDigitCardNo() {
        return lastFourDigitCardNo;
    }

    public void setLastFourDigitCardNo(String lastFourDigitCardNo) {
        this.lastFourDigitCardNo = lastFourDigitCardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
