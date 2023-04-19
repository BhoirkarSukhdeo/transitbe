package com.axisbank.transit.journey.repository;

import com.axisbank.transit.journey.model.DAO.JourneyModeDetailsDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyModeDetailsRepository extends JpaRepository<JourneyModeDetailsDAO, Long> {
}
