package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusTimeTableVer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusTimeTableVerRepository extends JpaRepository<BusTimeTableVer, Long> {
    List<BusTimeTableVer> findAllByTimeTableType_BusTimetableIdAndBusRoute_RouteCodeContainingIgnoreCaseOrBusStation_DisplayNameContainingIgnoreCase(String timeTableId, String routeCode, String stationName, Pageable paging);
}
