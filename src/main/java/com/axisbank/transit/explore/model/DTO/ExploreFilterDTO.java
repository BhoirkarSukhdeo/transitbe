package com.axisbank.transit.explore.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExploreFilterDTO {
    private String name;
    private String type;
    private Double validMin;
    private Double validMax;
    private Double minVal;
    private Double maxVal;
    private String selectedVal;

    private List<String> validVals;
    private List<String> listVals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValidMin() {
        return validMin;
    }

    public void setValidMin(Double validMin) {
        this.validMin = validMin;
    }

    public Double getValidMax() {
        return validMax;
    }

    public void setValidMax(Double validMax) {
        this.validMax = validMax;
    }

    public Double getMinVal() {
        return minVal;
    }

    public void setMinVal(Double minVal) {
        this.minVal = minVal;
    }

    public Double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Double maxVal) {
        this.maxVal = maxVal;
    }

    public List<String> getValidVals() {
        return validVals;
    }

    public void setValidVals(List<String> validVals) {
        this.validVals = validVals;
    }

    public List<String> getListVals() {
        return listVals;
    }

    public void setListVals(List<String> listVals) {
        this.listVals = listVals;
    }

    public String getSelectedVal() {
        return selectedVal;
    }

    public void setSelectedVal(String selectedVal) {
        this.selectedVal = selectedVal;
    }
}
