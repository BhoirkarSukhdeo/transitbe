package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import com.axisbank.transit.kmrl.model.DTO.NearByStationsDTOInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetroStationRepository extends JpaRepository<MetroStation, Long> {
    MetroStation findByStationId(String stationId);
    MetroStation findByStationCodeUpOrStationCodeDn(String stationCodeUp, String stationCodeDn);

    @Query(value = "SELECT SDO_GEOM.SDO_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL),\n" +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(ms.LATITUDE , ms.LONGITUDE , NULL), NULL, NULL), 0.005,'unit=METER')" +
            "AS sqlDist, ms.* FROM METRO_STATION ms WHERE SDO_WITHIN_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL),\n" +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(ms.LATITUDE , ms.LONGITUDE , NULL), NULL, NULL), :params) = 'TRUE' ORDER BY sqlDist ASC ", nativeQuery = true)
    List<NearByStationsDTOInterface> findAllStationsByLatLong(@Param("latitude") double latitude,
                                                              @Param("longitude") double longitude,
                                                              @Param("params") String params);
    MetroStation findByStationCode(String stationCode);

    List<MetroStation> findAllByDisplayNameContainingIgnoreCase(String displayName, Pageable paging);
}
