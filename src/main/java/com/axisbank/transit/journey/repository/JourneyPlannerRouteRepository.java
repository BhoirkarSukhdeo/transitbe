package com.axisbank.transit.journey.repository;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.journey.model.DAO.JourneyPlannerRouteDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyPlannerRouteRepository extends JpaRepository<JourneyPlannerRouteDAO, Long> {

    JourneyPlannerRouteDAO findByAuthenticationDAOAndIsActive(AuthenticationDAO authenticationDAO, Boolean isActive);
}
