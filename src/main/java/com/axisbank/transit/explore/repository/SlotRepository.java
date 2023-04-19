package com.axisbank.transit.explore.repository;

import com.axisbank.transit.explore.model.DAO.SlotDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<SlotDAO, Long> {
}
