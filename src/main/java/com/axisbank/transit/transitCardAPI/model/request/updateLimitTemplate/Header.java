package com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

    public SubHeader subHeader;

    public SubHeader getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(SubHeader subHeader) {
        this.subHeader = subHeader;
    }

    @Override
    public String toString() {
        return "Header{" +
                ", subHeader=" + subHeader +
                '}';
    }
}
