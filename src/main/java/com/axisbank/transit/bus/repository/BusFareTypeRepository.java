package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusFareType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusFareTypeRepository extends JpaRepository<BusFareType, Long> {
    BusFareType findByBusFareName(String name);
    BusFareType findByBusFareTypeId(String busFareTypeId);
    BusFareType findByBusFareTypeIdAndCurrentStatus(String busFareTypeId, String currStatus);
    List<BusFareType> findAllByIsActive(Boolean isActive);
}
