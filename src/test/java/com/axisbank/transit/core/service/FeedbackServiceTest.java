package com.axisbank.transit.core.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.MpinLog;
import com.axisbank.transit.core.model.DAO.FeedbackCategoryDAO;
import com.axisbank.transit.core.model.DAO.FeedbackDAO;
import com.axisbank.transit.core.model.request.FeedbackRequestDTO;
import com.axisbank.transit.core.repository.FeedbackCategoryRepository;
import com.axisbank.transit.core.repository.FeedbackRepository;
import com.axisbank.transit.core.service.impl.FeedbackServiceImpl;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class FeedbackServiceTest extends BaseTest {
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    private FeedbackDAO feedbackDAO;
    FeedbackCategoryDAO feedbackCategoryDAO;


    @Mock
    FeedbackCategoryRepository feedbackCategoryRepository;

    @Mock
    FeedbackRepository feedbackRepository;

    @Mock
    UserUtil userUtil;

    @InjectMocks
    @Autowired
    FeedbackServiceImpl feedbackService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CommonUtils.class);

        authenticationDAO = new AuthenticationDAO();

        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("8899899709");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        Set<ExploreDAO> exploreDAOSet = new HashSet<>();
        authenticationDAO.setExploreDAOSet(exploreDAOSet);
        Set<MpinLog> mpinLogSet = new HashSet<>();
        authenticationDAO.setMpins(mpinLogSet);
        authenticationDAO.setDaoUser(daoUser);

        feedbackDAO = new FeedbackDAO();
        feedbackDAO.setAuthenticationDAO(authenticationDAO);
        feedbackDAO.setDescription("feedDesc");

        feedbackCategoryDAO = new FeedbackCategoryDAO();
        feedbackCategoryDAO.setCategoryId("123");
        feedbackCategoryDAO.setDisplayName("feedbackCat");
        feedbackCategoryDAO.setDescription("description");
    }

    @Test
    public void saveFeedbackTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(feedbackCategoryRepository.findByCategoryId(any(String.class))).thenReturn(feedbackCategoryDAO);
        when(feedbackRepository.save(any(FeedbackDAO.class))).thenReturn(feedbackDAO);
        FeedbackRequestDTO feedbackRequestDTO = new FeedbackRequestDTO();
        feedbackRequestDTO.setDescription("feedDesc");
        feedbackRequestDTO.setCategoryId("123");
        feedbackService.saveFeedback(feedbackRequestDTO);
    }

    @Test
    public void getFeedbacksTest() throws Exception {
        PowerMockito.when(CommonUtils.getFullName(any(String.class), any(String.class), any(String.class))).thenReturn("raj");
        feedbackDAO.setCategoryDAO(feedbackCategoryDAO);
        feedbackDAO.setCreatedAt(new Date());
        List<FeedbackDAO> feedbackDAOList = new ArrayList<>();
        feedbackDAOList.add(feedbackDAO);
        Page<FeedbackDAO> page = new Page<FeedbackDAO>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super FeedbackDAO, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<FeedbackDAO> getContent() {
                return feedbackDAOList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @NotNull
            @Override
            public Iterator<FeedbackDAO> iterator() {
                return null;
            }
        };
        when(feedbackRepository.findAll(any(Pageable.class))).thenReturn(page);
        feedbackService.getFeedbacks(0, 10);
    }
}
