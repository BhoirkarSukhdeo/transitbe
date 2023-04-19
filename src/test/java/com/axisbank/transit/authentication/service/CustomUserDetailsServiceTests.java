package com.axisbank.transit.authentication.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.axisbank.transit.core.shared.constants.RoleConstants.PUBLISHER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTests extends BaseTest {
    private AuthenticationDAO authenticationDAO;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();

        authenticationDAO.setMobile("1122334455");
        authenticationDAO.setUserName("1122334455");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setRoles(new ArrayList<>());
        Set<ExploreDAO> exploreDAOSet = new HashSet<>();
        authenticationDAO.setExploreDAOSet(exploreDAOSet);
    }

    @Test
    public void loadUserByUsernameTest() throws Exception {
        Role role = new Role();
        role.setName(PUBLISHER);
        authenticationDAO.getRoles().add(role);
        SessionDAO sessionDAO = new SessionDAO();
        authenticationDAO.setSessionDAO(sessionDAO);
        when(authenticationRepository.findByUserNameIgnoreCaseAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        doNothing().when(authService).checkBlockedUser(any(SessionDAO.class));
        Assert.assertNotNull(customUserDetailsService.loadUserByUsername("1122334455"));
    }
}
