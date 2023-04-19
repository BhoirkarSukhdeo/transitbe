package com.axisbank.transit.journey.services.impl;

import com.axisbank.transit.core.shared.utils.DatabaseRawQueryUtils;
import com.axisbank.transit.journey.model.DAO.GeoTagEntity;
import com.axisbank.transit.journey.model.DTO.GeoJsonDTO;
import com.axisbank.transit.journey.model.requests.GeoTagRequest;
import com.axisbank.transit.journey.model.responses.GeoTagResponse;
import com.axisbank.transit.journey.services.GeoTagService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.axisbank.transit.core.shared.utils.CommonUtils.convertObjectToJsonString;
import static com.axisbank.transit.core.shared.utils.CommonUtils.generateRandomString;

@Slf4j
@Service
public class GeoTagServiceImpl implements GeoTagService {
    @Autowired
    DatabaseRawQueryUtils databaseRawQueryUtils;
    @Override
    public GeoTagResponse setPolygonFromGeoJson(GeoTagRequest geoTagRequest) {
        try {
            GeoJsonDTO geoJson = geoTagRequest.getGeojson();
            String geojsonString = convertObjectToJsonString(geoJson);
            String poly = databaseRawQueryUtils.generateGeomFromGeoJSON(geojsonString);
            WKTReader reader = new WKTReader();
            Geometry geom = reader.read(poly);
            GeoTagEntity geoTagEntity = new GeoTagEntity();
            geoTagEntity.setPolygon(geom);
            geoTagEntity.setLocationId(generateRandomString(10));
            geoTagEntity.setDescription(geoTagRequest.getDescription());
            geoTagEntity.setName(geoTagRequest.getName());
            GeoTagResponse geoTagResponse = new GeoTagResponse();
            geoTagResponse.setDescription(geoTagEntity.getDescription());
            geoTagResponse.setName(geoTagEntity.getName());
            geoTagResponse.setLocationId(geoTagEntity.getLocationId());
            return geoTagResponse;
        } catch (Exception ex){
            log.error("Exception in setPolygonFromGeoJson method: {}", ex.getMessage());
        }
        return null;
    }
}
