package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Slf4j
@Component
public class DatabaseRawQueryUtils {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public String generateGeomFromGeoJSON(String geoJson) {

        if (geoJson == null || geoJson.isEmpty()) {
            return null;
        } else {
            log.info("Generating geom from given geoJson as: {}", geoJson);
            String query = "select sdo_util.from_geojson('%s').Get_WKT() as geom from dual";
            query = String.format(query, geoJson);
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("Returning geom as: {}", resultSet.getString(1));
                    return resultSet.getString(1);
                } else {
                    log.info("ResultSet was empty so returning null");
                    return null;
                }
            } catch (SQLException e) {
                log.error("Exception in generateGeomFromGeoJSON method: {}", e.getMessage());
            }
        }
        return null;
    }
}
