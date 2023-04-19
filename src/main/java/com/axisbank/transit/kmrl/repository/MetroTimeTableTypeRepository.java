package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.kmrl.model.DAO.MetroTimeTableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetroTimeTableTypeRepository extends JpaRepository<MetroTimeTableType, Long> {
    MetroTimeTableType findByMtTimetableId(String timetabletypeId);
    MetroTimeTableType findByMtTimetableIdAndCurrentStatus(String timetabletypeId, String currStatus);
    List<MetroTimeTableType> findAllByIsActive(Boolean isActive);
}
