package com.axisbank.transit.journey.services;

import com.axisbank.transit.journey.model.requests.GeoTagRequest;
import com.axisbank.transit.journey.model.responses.GeoTagResponse;


public interface GeoTagService {
    GeoTagResponse setPolygonFromGeoJson(GeoTagRequest geoTagRequest);
}
