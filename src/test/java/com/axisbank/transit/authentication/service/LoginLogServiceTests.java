package com.axisbank.transit.authentication.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.LoginLog;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.repository.LoginLogRepository;
import com.axisbank.transit.authentication.service.impl.LoginLogServiceImpl;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;

import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGIN_STATUS_SUCCESS;
import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGIN_TYPE_OTP;

public class LoginLogServiceTests extends BaseTest {
    private AuthenticationDAO authenticationDAO;
    private LoginLog loginLog;
    LoginLogServiceImpl loginLogServicemock = mock(LoginLogServiceImpl.class);

    @Mock
    LoginLogRepository loginLogRepository;

    @Mock
    AuthenticationRepository authenticationRepository;

    @InjectMocks
    @Autowired
    LoginLogServiceImpl loginLogService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();

        authenticationDAO.setMobile("8899899709");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        Set<ExploreDAO> exploreDAOSet = new HashSet<>();
        authenticationDAO.setExploreDAOSet(exploreDAOSet);

        loginLog = new LoginLog();
        loginLog.setAuthenticationDAO(authenticationDAO);
        loginLog.setLoginLogRefId("12334");
        loginLog.setLoginType(LOGIN_TYPE_OTP);
        loginLog.setLoginStatus(LOGIN_STATUS_SUCCESS);
    }

    @Test
    public void addLoginLogCase1Test() {
        when(loginLogRepository.save(loginLog)).thenReturn(loginLog);
        loginLogService.addLoginLog(authenticationDAO, LOGIN_TYPE_OTP, LOGIN_STATUS_SUCCESS);
        Assert.assertNotNull(loginLog.getAuthenticationDAO());
    }

    @Test
    public void addLoginLogCase2Test() {
        when(loginLogRepository.save(loginLog)).thenReturn(loginLog);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        loginLogService.addLoginLog("pradeep", LOGIN_TYPE_OTP, LOGIN_STATUS_SUCCESS);
        Assert.assertNotNull(loginLog.getAuthenticationDAO());
    }

    @Test
    public void addLogoutLogCase1Test() {
        when(loginLogRepository.save(loginLog)).thenReturn(loginLog);
        loginLogService.addLogoutLog(authenticationDAO, LOGIN_TYPE_OTP, LOGIN_STATUS_SUCCESS);
        Assert.assertNotNull(loginLog.getAuthenticationDAO());
    }

    @Test
    public void addLogoutLogCase2Test() {
        when(loginLogRepository.save(loginLog)).thenReturn(loginLog);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        loginLogService.addLogoutLog("pradeep", LOGIN_TYPE_OTP, LOGIN_STATUS_SUCCESS);
        Assert.assertNotNull(loginLog.getAuthenticationDAO());
    }
}
