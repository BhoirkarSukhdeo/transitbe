package com.axisbank.transit.core.repository;

import com.axisbank.transit.core.model.DAO.FeedbackCategoryDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackCategoryRepository extends JpaRepository<FeedbackCategoryDAO, Long> {
    public FeedbackCategoryDAO findByCategoryId(String categoryId);
}
