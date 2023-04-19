package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DTO.UserConfigDTO;
import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;
import com.axisbank.transit.userDetails.model.DTO.UserDetailsDTO;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.service.impl.UserInfoServiceImpl;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonUtils.class)
public class UserInfoServiceTests extends BaseTest {

    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;

    @InjectMocks
    @Autowired
    UserInfoServiceImpl userInfoService;

    @Mock
    UserUtil userUtil;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GlobalConfigService globalConfigService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();

        CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();

        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);
    }

    @Test
    public void updateUserDetailsTest() throws Exception{

        UserDetailsDTO userDetailsDTO =  new UserDetailsDTO(
                "12233444",
                "pradeep sharma",
                "2233771199",
                "pradeeep111@gmail.com",
                Gender.MALE,
                "pradeeep111@gmail.com",
                LocalDate.of(1994, 11, 23),
                "SE",
                true,
                null,
                "transit_user",
                true,
                new ArrayList<>()
        );

        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(VALID_OCCUPATIONS);
        String json = "{\n" +
                "  \"occupations\": [\n" +
                "    \"Bussiness Professional\",\n" +
                "    \"Medical / Healthcare Professional\",\n" +
                "    \"Government / Civil Services\",\n" +
                "    \"Retired\",\n" +
                "    \"Educator\",\n" +
                "    \"Homemaker\",\n" +
                "    \"Hospitality\",\n" +
                "    \"Transportation\",\n" +
                "    \"Sales\",\n" +
                "    \"Student\",\n" +
                "    \"Technology / Engineer\",\n" +
                "    \"Other\"\n" +
                "  ]\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true)).thenReturn(globalConfigDTO);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = userInfoService.updateUserDetails(userDetailsDTO);
        Assert.assertNotNull(authenticationDAOResult);
    }

    @Test
    public void getLoggedInUserDetailsTest() throws Exception{
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(VALID_OCCUPATIONS);
        String json = "{\n" +
                "  \"occupations\": [\n" +
                "    \"Bussiness Professional\",\n" +
                "    \"Medical / Healthcare Professional\",\n" +
                "    \"Government / Civil Services\",\n" +
                "    \"Retired\",\n" +
                "    \"Educator\",\n" +
                "    \"Homemaker\",\n" +
                "    \"Hospitality\",\n" +
                "    \"Transportation\",\n" +
                "    \"Sales\",\n" +
                "    \"Student\",\n" +
                "    \"Technology / Engineer\",\n" +
                "    \"Other\"\n" +
                "  ]\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true)).thenReturn(globalConfigDTO);
        UserDetailsDTO userDetailsDTO = userInfoService.getLoggedInUserDetails();
        assertEquals("pradeeep123@gmail.com", userDetailsDTO.getEmail());
    }

    @Test
    public void getUserDetailsTest() throws Exception{
        String userId = "123";
        when(userRepository.findByUserIdAndIsActive(userId, true)).thenReturn(daoUser);
        UserDetailsDTO userDetailsDTO = userInfoService.getUserDetails(userId);
        assertEquals("pradeeep123@gmail.com", userDetailsDTO.getEmail());
    }

    @Test
    public void getUserConfigTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        UserConfigDTO result = userInfoService.getUserConfig();
        assertEquals(false, result.getPushNotificationsStatus());
    }

    @Test
    public void updateUserConfigurationTest() throws Exception {
        UserConfigurationDTO userConfigurationDTO = new UserConfigurationDTO();
        userConfigurationDTO.setTravelAlerts(true);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(userRepository.save(daoUser)).thenReturn(daoUser);
        DAOUser daoUser = userInfoService.updateUserConfiguration(userConfigurationDTO);
        Assert.assertNotNull(daoUser.getUserConfiguration());
    }

}
