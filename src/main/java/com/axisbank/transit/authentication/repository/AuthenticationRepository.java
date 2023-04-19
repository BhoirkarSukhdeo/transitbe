package com.axisbank.transit.authentication.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationRepository extends JpaRepository<AuthenticationDAO, Long> {
	AuthenticationDAO findByMobileAndIsActive(String mobile, Boolean isActive);
	AuthenticationDAO findByUserNameAndIsActive(String username, Boolean isActive);
	AuthenticationDAO findByUserNameIgnoreCaseAndIsActive(String username, Boolean isActive);
    List<AuthenticationDAO> findAllByUserTypeNotLike(String userType);
    List<AuthenticationDAO> findAllByIsActiveAndDeviceInfo_FcmTokenIsNotNull(boolean isActive);

    List<AuthenticationDAO> findByMobileIn(List<String> mobileList);

    AuthenticationDAO findByUserName(String username);
    AuthenticationDAO findByUserNameIgnoreCase(String username);
}