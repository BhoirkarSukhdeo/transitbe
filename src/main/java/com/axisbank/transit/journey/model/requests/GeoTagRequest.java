package com.axisbank.transit.journey.model.requests;

import com.axisbank.transit.journey.model.DTO.GeoJsonDTO;

public class GeoTagRequest {
    private String name;
    private GeoJsonDTO geojson;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoJsonDTO getGeojson() {
        return geojson;
    }

    public void setGeojson(GeoJsonDTO geojson) {
        this.geojson = geojson;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
