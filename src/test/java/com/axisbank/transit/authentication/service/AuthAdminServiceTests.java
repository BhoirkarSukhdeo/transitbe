package com.axisbank.transit.authentication.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DTO.AdminUserDTO;
import com.axisbank.transit.authentication.model.DTO.UserDetailsAdminDTO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.repository.RoleRepository;
import com.axisbank.transit.authentication.service.impl.AuthAdminServiceImpl;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.RoleConstants.ADMIN_ROLE;
import static com.axisbank.transit.core.shared.constants.RoleConstants.PUBLISHER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthAdminServiceTests extends BaseTest {
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    List<String> roleList;
    List<Role> dbRoleList;

    @Autowired
    @InjectMocks
    AuthAdminServiceImpl authAdminService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    UserUtil userUtil;

    @Mock
    UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        roleList = Arrays.asList(ADMIN_ROLE, PUBLISHER);
        dbRoleList = new ArrayList<>();
        for (String roleName: roleList) {
            Role role = new Role();
            role.setName(roleName);
            dbRoleList.add(role);
        }

        authenticationDAO = new AuthenticationDAO();

        daoUser = new DAOUser();
        daoUser.setUserId("123456");
        daoUser.setOccupation("SE");
        daoUser.setFirstName("xyz");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("1122334455");
        authenticationDAO.setUserName("1122334455");
        authenticationDAO.setUserType("portal-user");
        authenticationDAO.setEmail("xyz@gmail.com");
        authenticationDAO.setRoles(dbRoleList);
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setActive(true);
        authenticationDAO.setDaoUser(daoUser);
        daoUser.setAuthenticationDAO(authenticationDAO);
    }

    @Test
    public void createUserTest() throws Exception {
        UserDetailsAdminDTO userDetailsAdminDTO = new UserDetailsAdminDTO();
        userDetailsAdminDTO.setFirstName("xyz");
        userDetailsAdminDTO.setMobile("1122334455");
        userDetailsAdminDTO.setUserType("portal-user");
        userDetailsAdminDTO.setUsername("1122334455");
        userDetailsAdminDTO.setRoles(roleList);
        userDetailsAdminDTO.setGender(Gender.MALE);
        userDetailsAdminDTO.setEmail("xyz@gmail.com");

        when(roleRepository.findAllByNameInAndIsActive(roleList, true)).thenReturn(dbRoleList);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        authAdminService.createUser(userDetailsAdminDTO);
        Assert.assertEquals("1122334455", authenticationDAO.getMobile());
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDetailsAdminDTO userDetailsAdminDTO = new UserDetailsAdminDTO();
        userDetailsAdminDTO.setUserId("123456");
        userDetailsAdminDTO.setFirstName("xyz");
        userDetailsAdminDTO.setMobile("1122334455");
        userDetailsAdminDTO.setUserType("portal-user");
        userDetailsAdminDTO.setUsername("1122334455");
        userDetailsAdminDTO.setRoles(roleList);
        userDetailsAdminDTO.setGender(Gender.MALE);
        userDetailsAdminDTO.setEmail("xyz@gmail.com");
        userDetailsAdminDTO.setActive(true);

        when(userUtil.getLoggedInUserName()).thenReturn("1122334455");
        when(userRepository.findByUserId(any(String.class))).thenReturn(daoUser);
        when(roleRepository.findAllByNameInAndIsActive(roleList, true)).thenReturn(dbRoleList);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        authAdminService.updateUser(userDetailsAdminDTO);
        Assert.assertEquals("1122334455", authenticationDAO.getMobile());
    }

    @Test
    public void getAllRolesTest() throws Exception {
        when(roleRepository.findAllByIsActive(true)).thenReturn(dbRoleList);
        List<String> result = authAdminService.getAllRoles();
        Assert.assertEquals(true, result.contains(PUBLISHER));
    }

    @Test
    public void getAllAdminUsersTest() throws Exception {
        List<AuthenticationDAO> authenticationDAOS = new ArrayList<>();
        authenticationDAOS.add(authenticationDAO);
        when(authenticationRepository.findAllByUserTypeNotLike(TransitCardAPIConstants.TRANSIT_USER)).thenReturn(authenticationDAOS);
        List<AdminUserDTO> result = authAdminService.getAllAdminUsers();
        Assert.assertEquals("1122334455", result.get(0).getMobile());
    }
}
