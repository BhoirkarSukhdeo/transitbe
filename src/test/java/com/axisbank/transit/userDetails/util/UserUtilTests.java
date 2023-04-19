package com.axisbank.transit.userDetails.util;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.util.JwtUtil;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
@PowerMockIgnore("javax.crypto.*")
public class UserUtilTests extends BaseTest {

    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;

    @InjectMocks
    @Autowired
    UserUtil userUtil;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    @Autowired
    JwtUtil jwtUtil;

    @Mock
    RedisClient redisClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();

        CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();

        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);
        daoUser.setUserId("transit123");

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setUserName("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);

        userUtil.setSecret("123456");
        jwtUtil.setSecret("123456");
        jwtUtil.setJwtExpirationInSec(3000);
        jwtUtil.setRefreshExpirationDateInSec(6000);
        jwtUtil.setIdleSessionTimeOut(600);
    }

    @Test
    public void getAuthObjectTest () throws Exception{

        User user = new User("2233771199", "", new ArrayList<>());
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getLoggedinUser()).thenReturn(user);
        when(authenticationRepository.findByUserNameIgnoreCaseAndIsActive(authenticationDAO.getMobile(), true)).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = userUtil.getAuthObject();
        assertEquals("pradeeep123@gmail.com", authenticationDAOResult.getEmail());
    }

    @Test
    public void getLoggedInUserNameTest() throws Exception{
        User user = new User("2233771199", "", new ArrayList<>());
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getLoggedinUser()).thenReturn(user);

        String userName = userUtil.getLoggedInUserName();
        Assert.assertEquals("2233771199", userName);
    }

    @Test
    public void validateWithLoggedInUserIdTest() throws Exception{
        User user = new User("2233771199", "", new ArrayList<>());
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getLoggedinUser()).thenReturn(user);
        when(authenticationRepository.findByUserNameIgnoreCaseAndIsActive(authenticationDAO.getMobile(), true)).thenReturn(authenticationDAO);
        Boolean isUserExist = userUtil.validateWithLoggedInUserId("transit123");
        assertEquals(true, isUserExist);

    }

    @Test
    public void getUserIdFromTokenTest() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5MTg4OTk4OTk3MDkiLCJpc1JlZnJlc2hUb2tlbiI6dHJ1ZSwiZXhwIjoxNjEwMTU5ODk0LCJpYXQiOjE2MDk3NDk4Mjh9.mNuDOe3ZIdr6p7Aknx58MeCoqLfKqGJd_3Y9JVdFZvBsoILY2Mo1kaP3x9IoXpQ3d5SrQ4qIZN4FPv5E0zM7zA";
        when(userRepository.findByAuthenticationDAO_UserName(any(String.class))).thenReturn(daoUser);
        String username = userUtil.getUserIdFromToken(jwtToken);
        Assert.assertEquals("transit123", username);
    }

    @Test
    public void getUserIdFromNewTokenTest() throws Exception {
        User userDetails = new User(authenticationDAO.getUserName(), "", new ArrayList<>());
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        doNothing().when(redisClient).deletePattern(any(String.class));
        String accessToken = jwtUtil.generateToken(userDetails);
        when(userRepository.findByAuthenticationDAO_UserName(any(String.class))).thenReturn(daoUser);
        String username = userUtil.getUserIdFromToken(accessToken);
        Assert.assertEquals("transit123", username);
    }

}
