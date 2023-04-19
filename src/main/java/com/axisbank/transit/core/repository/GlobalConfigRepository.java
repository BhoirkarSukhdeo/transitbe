package com.axisbank.transit.core.repository;

import com.axisbank.transit.core.model.DAO.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, Long> {
    GlobalConfig findByKeyAndIsActive(String key, boolean isActive);
    List<GlobalConfig> findAllByIsActive(boolean isActive);
}
