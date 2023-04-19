package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusRoute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.ContentHandler;
import java.util.List;

@Repository
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {
    BusRoute findByRouteCode(String routeCode);
    List<BusRoute> findAllByBusStationSet_StationId(String busStation);
    List<BusRoute> findAllByBusStationSet_StationIdAndRouteIdIn(String busStation, List<String> busRouteIds);
    List<BusRoute> findAllByBusType(String busType);
    List<BusRoute> findByIdIn(List<Long> ids);
    List<BusRoute> findAllByRouteCodeContainingIgnoreCaseOrVehicleNumberContainingIgnoreCaseOrBusTypeContainingIgnoreCase(String routeCode, String vehicleNumber, String busType, Pageable paging);
}
