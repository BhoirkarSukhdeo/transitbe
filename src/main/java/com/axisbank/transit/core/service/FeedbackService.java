package com.axisbank.transit.core.service;

import com.axisbank.transit.core.model.request.FeedbackRequestDTO;
import com.axisbank.transit.core.model.response.FeedbackResponseDTO;

import java.util.List;

public interface FeedbackService {
    void saveFeedback(FeedbackRequestDTO feedbackRequestDTO) throws Exception;

    List<FeedbackResponseDTO> getFeedbacks(int page, int size) throws Exception;
}
