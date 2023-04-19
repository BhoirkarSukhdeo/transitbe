package com.axisbank.transit.bus.repository;

import com.axisbank.transit.bus.model.DAO.BusTimeTableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusTimetableTypeRepository extends JpaRepository<BusTimeTableType, Long> {
    BusTimeTableType findByTimeTableName(String name);
    BusTimeTableType findByBusTimetableId(String timetabletypeId);
    BusTimeTableType findByBusTimetableIdAndCurrentStatus(String timetabletypeId, String currStatus);
    List<BusTimeTableType> findAllByIsActive(Boolean isActive);
}
