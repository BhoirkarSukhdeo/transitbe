package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.journey.model.DTO.CoordinatesDto;

public class DistanceHelper {
    public static double getHaversine(CoordinatesDto srcCoordinate, CoordinatesDto destCoordinate) {
        double lat1 = srcCoordinate.getLatitude();
        double lon1 = srcCoordinate.getLongitude();
        double lat2 = destCoordinate.getLatitude();
        double lon2 = destCoordinate.getLongitude();

        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return (rad * c) * 1000;
    }
}
