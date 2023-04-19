package com.axisbank.transit.core.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.NotificationDAO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationDAO, Long> {
    List<NotificationDAO> findAllByAuthenticationDAO(AuthenticationDAO authenticationDAO, Pageable paging);
    NotificationDAO findByNotificationRefIdAndAuthenticationDAO(String notificatioRefId, AuthenticationDAO authenticationDAO);
}
