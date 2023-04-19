package com.axisbank.transit.transitCardAPI.repository;

import com.axisbank.transit.transitCardAPI.model.DAO.CardLimitDetailsDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardLimitDetailsRepository extends JpaRepository<CardLimitDetailsDAO, Long> {
}
