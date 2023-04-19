package com.axisbank.transit.core.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.FeedbackCategoryDAO;
import com.axisbank.transit.core.model.DAO.FeedbackDAO;
import com.axisbank.transit.core.model.request.FeedbackRequestDTO;
import com.axisbank.transit.core.model.response.FeedbackResponseDTO;
import com.axisbank.transit.core.repository.FeedbackCategoryRepository;
import com.axisbank.transit.core.repository.FeedbackRepository;
import com.axisbank.transit.core.service.FeedbackService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    FeedbackCategoryRepository feedbackCategoryRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    UserUtil userUtil;

    public void saveFeedback(FeedbackRequestDTO feedbackRequestDTO) throws Exception {
        log.info("Request received in saveFeedback method");
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            FeedbackCategoryDAO feedbackCategoryDAO = feedbackCategoryRepository.findByCategoryId(feedbackRequestDTO.getCategoryId());
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            feedbackDAO.setDescription(feedbackRequestDTO.getDescription());
            feedbackDAO.setAuthenticationDAO(authenticationDAO);
            feedbackDAO.setCategoryDAO(feedbackCategoryDAO);
            feedbackRepository.save(feedbackDAO);
        } catch (Exception exception) {
            log.error("Exception in saving feedback: {}", exception.getMessage());
            throw exception;
        }
    }

    public List<FeedbackResponseDTO> getFeedbacks(int page, int size) throws Exception {
        log.info("Request received in getFeedbacks method");
        List<FeedbackResponseDTO> feedbackRequestDTOList = new ArrayList<>();
        Pageable requestedPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        try {
            List<FeedbackDAO> feedbackDAOList = feedbackRepository.findAll(requestedPage).getContent();
            for (FeedbackDAO feedbackDAO : feedbackDAOList) {
                FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO();
                feedbackResponseDTO.setCategoryId(feedbackDAO.getCategoryDAO().getCategoryId());
                feedbackResponseDTO.setCategoryName(feedbackDAO.getCategoryDAO().getDisplayName());
                feedbackResponseDTO.setDescription(feedbackDAO.getDescription());
                DAOUser daoUser = feedbackDAO.getAuthenticationDAO().getDaoUser();
                feedbackResponseDTO.setCreatedBy(CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName()));
                SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                String createdDate = formatter.format(feedbackDAO.getCreatedAt());
                feedbackResponseDTO.setCreatedAt(createdDate);
                feedbackRequestDTOList.add(feedbackResponseDTO);
            }
        } catch (Exception exception) {
            log.error("Exception in getting feedback list: {}", exception.getMessage());
            throw exception;
        }
        return feedbackRequestDTOList;
    }
}
