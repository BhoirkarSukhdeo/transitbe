package com.axisbank.transit.userDetails.repository;

import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {
}
