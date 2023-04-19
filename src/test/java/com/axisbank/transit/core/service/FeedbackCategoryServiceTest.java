package com.axisbank.transit.core.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.model.DAO.FeedbackCategoryDAO;
import com.axisbank.transit.core.model.DAO.FeedbackDAO;
import com.axisbank.transit.core.model.response.FeedbackCategoryDTO;
import com.axisbank.transit.core.repository.FeedbackCategoryRepository;
import com.axisbank.transit.core.service.impl.FeedbackCategoryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.Any;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class FeedbackCategoryServiceTest extends BaseTest {
    FeedbackCategoryDAO feedbackCategoryDAO;

    @InjectMocks
    @Autowired
    FeedbackCategoryServiceImpl feedbackCategoryService;

    @Mock
    FeedbackCategoryRepository feedbackCategoryRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        feedbackCategoryDAO = new FeedbackCategoryDAO();
        feedbackCategoryDAO.setCategoryId("123");
        feedbackCategoryDAO.setDescription("Description");
        feedbackCategoryDAO.setDisplayName("Info");
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<FeedbackCategoryDAO> feedbackCategoryDAOList = new ArrayList<>();
        feedbackCategoryDAOList.add(feedbackCategoryDAO);
        when(feedbackCategoryRepository.findAll()).thenReturn(feedbackCategoryDAOList);
        Assert.assertNotNull(feedbackCategoryService.getCategories());
    }

    @Test
    public void saveFeedbackCategory() throws Exception {
        List<FeedbackCategoryDAO> feedbackCategoryDAOList = new ArrayList<>();
        FeedbackCategoryDTO feedbackCategoryDTO = new FeedbackCategoryDTO();
        feedbackCategoryDTO.setCategoryId("123");
        feedbackCategoryDTO.setDescription("Description");
        feedbackCategoryDTO.setCategoryName("Info");
        when(feedbackCategoryRepository.save(any(FeedbackCategoryDAO.class))).thenReturn(feedbackCategoryDAO);
        feedbackCategoryService.saveFeedbackCategory(feedbackCategoryDTO);
    }
}
