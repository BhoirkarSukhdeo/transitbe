package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusFareVer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusFareVerRepository extends JpaRepository<BusFareVer, Long> {
    List<BusFareVer> findAllByBusFareType_BusFareTypeIdAndBusRoute_RouteCodeContainingIgnoreCaseOrFromBusStation_DisplayNameContainingIgnoreCaseOrToBusStation_DisplayNameContainingIgnoreCase(String busFareTypeId, String busRouteName, String fromStationName, String toStationName, Pageable paging);
}
