package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusRoute;
import com.axisbank.transit.bus.model.DAO.BusStation;
import com.axisbank.transit.bus.model.DTO.NearByBusStationsDTOInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusStationRepository extends JpaRepository<BusStation, Long> {
    BusStation findByStationCode(String stationCode);
    @Query(value = "SELECT SDO_GEOM.SDO_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL), " +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(bs.LATITUDE , bs.LONGITUDE , NULL), NULL, NULL), 0.005,'unit=METER')" +
            "AS sqlDist, bs.* FROM bus_station bs WHERE SDO_WITHIN_DISTANCE(SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:latitude, :longitude, NULL), NULL, NULL), " +
            "SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(bs.LATITUDE , bs.LONGITUDE , NULL), NULL, NULL), :params) = 'TRUE'", nativeQuery = true)
    List<NearByBusStationsDTOInterface> findAllStationsByLatLong(@Param("latitude") double latitude,
                                                                 @Param("longitude") double longitude,
                                                                 @Param("params") String params);
    BusStation findByStationId(String stationId);

    List<BusStation> findAllByDisplayNameContainingIgnoreCase(String displayName, Pageable paging);
}
