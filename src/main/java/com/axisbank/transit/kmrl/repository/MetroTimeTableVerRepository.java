package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.kmrl.model.DAO.MetroTimeTableVer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetroTimeTableVerRepository extends JpaRepository<MetroTimeTableVer, Long> {
    List<MetroTimeTableVer> findAllByTrip_TimeTableType_MtTimetableIdAndStationNameContainingIgnoreCaseOrMetroLineNameContainingIgnoreCase(String timetabletypeId, String stationName, String metroLineName, Pageable paging);
}
