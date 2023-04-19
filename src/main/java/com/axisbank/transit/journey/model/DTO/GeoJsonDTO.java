package com.axisbank.transit.journey.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GeoJsonDTO {
    @JsonProperty("type")
    private String type;
    @JsonProperty("coordinates")
    private List<List<List<Double>>> coordinates = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }
}