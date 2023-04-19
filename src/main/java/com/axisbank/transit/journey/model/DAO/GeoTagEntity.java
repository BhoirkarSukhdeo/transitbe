package com.axisbank.transit.journey.model.DAO;

import org.locationtech.jts.geom.Geometry;

public class GeoTagEntity{
    private Geometry polygon;

    private String locationId;

    private String description;

    private String name;

    public Geometry getPolygon() {
        return polygon;
    }

    public void setPolygon(Geometry polygon) {
        this.polygon = polygon;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
