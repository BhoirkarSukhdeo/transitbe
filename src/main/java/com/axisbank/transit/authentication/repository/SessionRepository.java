package com.axisbank.transit.authentication.repository;

import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionDAO, Long> {
}
