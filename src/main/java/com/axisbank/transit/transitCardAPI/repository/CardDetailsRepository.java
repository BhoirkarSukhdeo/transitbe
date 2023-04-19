package com.axisbank.transit.transitCardAPI.repository;

import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDetailsRepository extends JpaRepository<CardDetailsDAO, Long> {
    CardDetailsDAO findByCardToken(String cardToken);
}
