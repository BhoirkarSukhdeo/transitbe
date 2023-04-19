package com.axisbank.transit.authentication.repository;

import com.axisbank.transit.authentication.model.DAO.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
