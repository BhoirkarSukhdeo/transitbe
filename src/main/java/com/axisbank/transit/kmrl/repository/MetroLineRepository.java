package com.axisbank.transit.kmrl.repository;

import com.axisbank.transit.kmrl.model.DAO.MetroLine;
import com.axisbank.transit.kmrl.model.DAO.MetroStation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetroLineRepository extends JpaRepository<MetroLine, Long> {
    List<MetroLine> findAllByDisplayNameContainingIgnoreCaseOrLineCodeContainingIgnoreCase(String displayName, String lineCode, Pageable paging);

    MetroLine findByLineId(String lineId);
}
