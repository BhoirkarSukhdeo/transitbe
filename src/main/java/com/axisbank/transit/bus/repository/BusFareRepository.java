package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusFare;
import com.axisbank.transit.bus.model.DAO.BusRoute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusFareRepository extends JpaRepository<BusFare, Long> {

    @Query(value = "SELECT DISTINCT route_id FROM bus_fare ORDER BY route_id", nativeQuery = true)
    List<Long> findDistinctRouteIds(Pageable pageable);

    List<BusFare> findAllByBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(String busRouteName, String fromStationName, String toStationName, Pageable paging);

    BusFare findByFareId(String fareId);
}
