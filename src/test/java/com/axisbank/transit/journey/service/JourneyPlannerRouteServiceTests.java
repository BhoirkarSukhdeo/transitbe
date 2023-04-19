package com.axisbank.transit.journey.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.journey.model.DAO.JourneyModeDetailsDAO;
import com.axisbank.transit.journey.model.DAO.JourneyPlannerRouteDAO;
import com.axisbank.transit.journey.model.DTO.JourneyModeDetails;
import com.axisbank.transit.journey.model.DTO.JourneyPlannerRouteDTO;
import com.axisbank.transit.journey.repository.JourneyPlannerRouteRepository;
import com.axisbank.transit.journey.services.impl.JourneyPlannerRouteServiceImpl;
import com.axisbank.transit.kmrl.service.TimeTableService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

import static com.axisbank.transit.core.shared.utils.CommonUtils.addSecondsToTime;
import static com.axisbank.transit.core.shared.utils.CommonUtils.getTimeDiff;
import static com.axisbank.transit.journey.constants.JourneyTypes.WALK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JourneyPlannerRouteServiceTests extends BaseTest {
    AuthenticationDAO authenticationDAO;
    DAOUser daoUser;
    JourneyPlannerRouteDAO journeyPlannerRouteDAO;
    JourneyModeDetailsDAO journeyModeDetailsDAO;
    List<JourneyModeDetailsDAO> journeyModeDetailsDAOList;

    @Mock
    UserUtil userUtil;

    @Mock
    JourneyPlannerRouteRepository journeyPlannerRouteRepository;

    @Mock
    TimeTableService timeTableService;

    @Mock
    BusTimeTableService busTimeTableService;

    @InjectMocks
    @Autowired
    JourneyPlannerRouteServiceImpl journeyPlannerRouteService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();
        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);

        journeyPlannerRouteDAO = new JourneyPlannerRouteDAO();
        journeyPlannerRouteDAO.setJourneyPlannerId("123");
        journeyPlannerRouteDAO.setAuthenticationDAO(authenticationDAO);
        journeyPlannerRouteDAO.setAmount(23.5);
        journeyPlannerRouteDAO.setArrivalTime(Time.valueOf("02:20:00"));
        journeyPlannerRouteDAO.setDepartureTime(Time.valueOf("02:20:00"));
        journeyPlannerRouteDAO.setTotalDistance(34.5);
        journeyPlannerRouteDAO.setAmount(45);
        journeyPlannerRouteDAO.setTotalDuration(23);

        journeyModeDetailsDAO = new JourneyModeDetailsDAO(
                23.5,Time.valueOf("02:30:00"),"walk","abc","xyz","route",34.5,20.3,"Aluva",1,20.3,34.5,45.6,67.4,Time.valueOf("03:30:00"),
                "source123","dest123",true,"xyz123",true,journeyPlannerRouteDAO
        );
        journeyModeDetailsDAOList = new ArrayList<>();
        journeyModeDetailsDAOList.add(journeyModeDetailsDAO);
        journeyPlannerRouteDAO.setJourneyModeDetailsDAOList(journeyModeDetailsDAOList);

    }

    @Test
    public void saveJourneyPlannerRouteTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        JourneyPlannerRouteDTO journeyPlannerRouteDTO = new JourneyPlannerRouteDTO();
        JourneyModeDetails journeyModeDetails = new JourneyModeDetails();
        journeyModeDetails.setType(WALK);
        journeyModeDetails.setDistance(23.4);
        journeyModeDetails.setSourceLatitude(34.5);
        journeyModeDetails.setSourceLongitude(12.3);
        journeyModeDetails.setDestinationLatitude(56.5);
        journeyModeDetails.setDestinationLongitude(34.5);
        journeyModeDetails.setTime(Time.valueOf("07:30:00"));
        journeyModeDetails.setEstimatedArrivalTime(Time.valueOf("02:30:00"));
        journeyModeDetails.setTravelTime(23.5);
        journeyModeDetails.setSourceId("123");
        journeyModeDetails.setDestinationId("123");

        journeyPlannerRouteDTO.setAmount(12.3);
        journeyPlannerRouteDTO.setArrivalTime(Time.valueOf("02:20:00"));
        journeyPlannerRouteDTO.setDepartureTime(Time.valueOf("02:20:00"));
        journeyPlannerRouteDTO.setTotalDistance(45.5);
        journeyPlannerRouteDTO.setTotalDuration(78);
        SortedSet<JourneyModeDetails> journeyModes =
                new TreeSet<>(Comparator.comparing(JourneyModeDetails::getTime));
        journeyModes.add(journeyModeDetails);
        journeyPlannerRouteDTO.setJourneyModeDetails(new ArrayList<>(journeyModes));
        when(journeyPlannerRouteRepository.save(any(JourneyPlannerRouteDAO.class))).thenReturn(journeyPlannerRouteDAO);
        journeyPlannerRouteService.saveJourneyPlannerRoute(journeyPlannerRouteDTO);
    }

    @Test
    public void getJourneyPlannerRouteTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(any(AuthenticationDAO.class), any(Boolean.class))).thenReturn(journeyPlannerRouteDAO);
        Assert.assertNotNull(journeyPlannerRouteService.getJourneyPlannerRoute());
    }

    @Test
    public void endJourneyTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(journeyPlannerRouteRepository.findByAuthenticationDAOAndIsActive(any(AuthenticationDAO.class), any(Boolean.class))).thenReturn(journeyPlannerRouteDAO);
        when(journeyPlannerRouteRepository.save(any(JourneyPlannerRouteDAO.class))).thenReturn(journeyPlannerRouteDAO);
        journeyPlannerRouteService.endJourney();
    }
}
