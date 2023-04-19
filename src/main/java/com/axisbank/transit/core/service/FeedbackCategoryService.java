package com.axisbank.transit.core.service;

import com.axisbank.transit.core.model.response.FeedbackCategoryDTO;

import java.util.List;

public interface FeedbackCategoryService {
    List<FeedbackCategoryDTO> getCategories() throws Exception;

    void saveFeedbackCategory(FeedbackCategoryDTO feedbackCategoryDTO) throws Exception;
}
