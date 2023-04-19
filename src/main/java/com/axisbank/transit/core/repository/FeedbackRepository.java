package com.axisbank.transit.core.repository;

import com.axisbank.transit.core.model.DAO.FeedbackDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackDAO, Long> {


}
