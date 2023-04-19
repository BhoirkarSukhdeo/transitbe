package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.core.model.DAO.FeedbackCategoryDAO;
import com.axisbank.transit.core.model.response.FeedbackCategoryDTO;
import com.axisbank.transit.core.repository.FeedbackCategoryRepository;
import com.axisbank.transit.core.service.FeedbackCategoryService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FeedbackCategoryServiceImpl implements FeedbackCategoryService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FeedbackCategoryRepository feedbackCategoryRepository;

    public List<FeedbackCategoryDTO> getCategories() throws Exception {
        log.info("Request received in getCategories method");
        List<FeedbackCategoryDTO> feedbackCategoryDTOList = new ArrayList<>();
        try {
            List<FeedbackCategoryDAO> feedbackCategoryDAOList = feedbackCategoryRepository.findAll();
            for (FeedbackCategoryDAO feedbackCategoryDAO: feedbackCategoryDAOList) {
                FeedbackCategoryDTO feedbackCategoryDTO = modelMapper.map(feedbackCategoryDAO, FeedbackCategoryDTO.class);
                feedbackCategoryDTOList.add(feedbackCategoryDTO);
            }
        } catch (Exception exception) {
            log.error("Exception in getting feedback categories: {}", exception.getMessage());
            throw exception;
        }
        return feedbackCategoryDTOList;
    }

    public void saveFeedbackCategory(FeedbackCategoryDTO feedbackCategoryDTO) throws Exception {
        log.info("Request received in saveFeedbackCategory method: "+feedbackCategoryDTO);
        try {
            FeedbackCategoryDAO feedbackCategoryDAO = new FeedbackCategoryDAO();
            feedbackCategoryDAO.setDisplayName(feedbackCategoryDTO.getCategoryName());
            feedbackCategoryDAO.setDescription(feedbackCategoryDTO.getDescription());
            feedbackCategoryDAO.setCategoryId(CommonUtils.generateRandomString(30));
            feedbackCategoryRepository.save(feedbackCategoryDAO);
        } catch (Exception exception) {
            log.error("Exception in saving feedback category: {}", exception.getMessage());
            throw exception;
        }
    }
}
