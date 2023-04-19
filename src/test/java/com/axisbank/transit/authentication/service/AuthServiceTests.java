package com.axisbank.transit.authentication.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.constants.RegistrationType;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.MpinLog;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.model.DTO.AuthDTO;
import com.axisbank.transit.authentication.model.DTO.SetMpinDTO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.repository.MpinRepository;
import com.axisbank.transit.authentication.repository.RoleRepository;
import com.axisbank.transit.authentication.repository.SessionRepository;
import com.axisbank.transit.authentication.service.impl.AuthServiceImpl;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.FinacleClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.request.GetCustomer;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCustomerDtlsRequest.GetCustomerDtlsRequest;
import com.axisbank.transit.transitCardAPI.model.request.getEntityDoc.GetEntityDocRequest;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.model.Customer;
import org.json.JSONObject;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationSetupData.class, BCrypt.class, EncryptionUtil.class})
@PowerMockIgnore("javax.crypto.*")
public class AuthServiceTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();

    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PaymentService paymentService;

    @Mock
    SessionRepository sessionRepository;

    @InjectMocks
    @Autowired
    AuthServiceImpl authService;

    @Mock
    UserUtil userUtil;

    @Mock
    MpinRepository mpinRepository;

    @Mock
    GlobalConfigService globalConfigService;

    @Mock
    ExploreRepository exploreRepository;

    @Mock
    FinacleClient finacleClient;

    @Mock
    TransitCardTxnService transitCardTxnService;

    @Mock
    private TransitCardClient transitCardClient;

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

        authenticationDAO.setMobile("8899899709");
        authenticationDAO.setUserName("8899899709");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        Set<ExploreDAO> exploreDAOSet = new HashSet<>();
        authenticationDAO.setExploreDAOSet(exploreDAOSet);
        Set<MpinLog> mpinLogSet = new HashSet<>();
        authenticationDAO.setMpins(mpinLogSet);
        authenticationDAO.setDaoUser(daoUser);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
    }

    @Test
    public void saveUserNoCardNotCustomerTest() throws Exception{
        AuthDTO authDTO = new AuthDTO();
        authDTO.setMobile("2356341188");
        authDTO.setDob(LocalDate.of(1995, 12, 24));
        authDTO.setEmailId("pradeep123@gmail.com");
        authDTO.setLastFourDigitCardNumber("2345");
        authDTO.setName("Pradeep Sharma");
        authDTO.setRegistrationType(RegistrationType.NoCardNotCustomer);

        Customer customer =   new Customer();
        customer.setFirstName("Pradeep");
        customer.setId("123");

        mockOccupations();

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);

        when(paymentService.createCustomer(any(String.class), any(String.class))).thenReturn(customer);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(boolean.class))).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = authService.saveUser(authDTO);
        Assert.assertNotNull(authenticationDAOResult);
    }

    private void mockOccupations() throws Exception {
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
        JsonNode jsonNode = mapper.readTree(json);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true)).thenReturn(globalConfigDTO);
    }

    @Test
    public void saveUserhasCardWithNumberTest() throws Exception {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setMobile("2356341188");
        authDTO.setLastFourDigitCardNumber("2345");
        authDTO.setRegistrationType(RegistrationType.hasCardWithNumber);

        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");

        Customer customer =   new Customer();
        customer.setFirstName("Pradeep");
        customer.setId("123");

        mockOccupations();

        String cardVerificationResJson = "{\n" +
                "  \"CardVerificationResponse\": {\n" +
                "    \"CardVerificationResult\": {\n" +
                "      \"Result\": \"Success\",\n" +
                "      \"ReturnCode\": \"2\",\n" +
                "      \"ReturnDescription\": \"Successfully completed.\",\n" +
                "      \"ErrorDetail\": {},\n" +
                "      \"CardList\": {\n" +
                "        \"string\": \"6078572352968749\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String customerJson = "{\n" +
                "  \"GetCustomerResponse\": {\n" +
                "    \"GetCustomerResult\": {\n" +
                "      \"Result\": \"Success\",\n" +
                "      \"ReturnCode\": 2,\n" +
                "      \"ReturnDescription\": \"Success\",\n" +
                "      \"ErrorDetail\": \"\",\n" +
                "      \"Customer\": {\n" +
                "        \"CustomerNo\": \"3544\",\n" +
                "        \"OneClickId\": \"\",\n" +
                "        \"Name\": \"HARI\",\n" +
                "        \"MidName\": \"\",\n" +
                "        \"Surname\": \"PRASAD\",\n" +
                "        \"BirthDate\": \"19890112\",\n" +
                "        \"FatherName\": \"\",\n" +
                "        \"MotherMaidenName\": \"testa\",\n" +
                "        \"Nationality\": \"IN\",\n" +
                "        \"PassportNo\": \"\",\n" +
                "        \"PassportIssuedBy\": \"\",\n" +
                "        \"PassportDateOfIssue\": \"19000101\",\n" +
                "        \"PassportDateOfExpire\": \"19000101\",\n" +
                "        \"PassportControlPeriod\": 0,\n" +
                "        \"EmergencyContactPersonNameSurname\": \"\",\n" +
                "        \"ResidenceCountryCode\": \"\",\n" +
                "        \"BirthCity\": \"\",\n" +
                "        \"BirthPlace\": \"\",\n" +
                "        \"Email\": \"ani@gmail.com\",\n" +
                "        \"CustomerType\": \"N\",\n" +
                "        \"Gender\": \"M\",\n" +
                "        \"CommunicationLanguage\": \"EN\",\n" +
                "        \"SendSMS\": \"\",\n" +
                "        \"SendEMail\": \"\",\n" +
                "        \"MobileNo\": \"919821001144\",\n" +
                "        \"WorkPlace\": \"\",\n" +
                "        \"Occupation\": \"Other\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode cardVerificationResJsonNode = mapper.readTree(cardVerificationResJson);
        JsonNode customerJsonNode = mapper.readTree(customerJson);
        when(transitCardClient.cardVerificationResp(any(CardVerificationRespRequest.class))).thenReturn(cardVerificationResJsonNode);
        when(transitCardClient.getCustomer(any(GetCustomer.class))).thenReturn(customerJsonNode);

        TransitCardInfoDTO transitCardInfoDTO = new TransitCardInfoDTO();
        transitCardInfoDTO.setCustomerNo("501");
        transitCardInfoDTO.setCardStatCode("xyz");
        transitCardInfoDTO.setCardStatSubCode("abc");
        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(transitCardInfoDTO);

        when(transitCardTxnService.getBlockStatus(any(String.class),any(String.class))).thenReturn("Active");


        when(transitCardTxnService.verifyCardBin(any(String.class))).thenReturn(true);
        when(transitCardTxnService.isRegistrationAllowed(any(String.class))).thenReturn(true);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);

        when(paymentService.createCustomer(any(String.class), any(String.class))).thenReturn(customer);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(boolean.class))).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = authService.saveUser(authDTO);
        Assert.assertNotNull(authenticationDAOResult);
    }

    @Test
    public void saveUserHasCardWithoutNumberTest() throws Exception{
        AuthDTO authDTO = new AuthDTO();
        authDTO.setMobile("2356341188");
        authDTO.setDob(LocalDate.of(1995, 12, 24));
        authDTO.setName("Pradeep");
        authDTO.setRegistrationType(RegistrationType.hasCardWithoutNumber);

        Customer customer =   new Customer();
        customer.setFirstName("Pradeep");
        customer.setId("123");

        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");

        mockOccupations();

        String cardDetailsJson = "{\n" +
                "  \"CompleteStatus\": \"Success\",\n" +
                "  \"CardNo\": \"11112222333334444\",\n" +
                "  \"CustomerNo\": \"123456\"\n" +
                "}";

        JSONObject cardDetailsJsonNode = new JSONObject(cardDetailsJson);
        when(transitCardTxnService.fetchCustomerCardInfo(
                any(String.class), any(String.class),any(String.class),any(String.class))).thenReturn(cardDetailsJsonNode);

        String customerJson = "{\n" +
                "  \"GetCustomerResponse\": {\n" +
                "    \"GetCustomerResult\": {\n" +
                "      \"Result\": \"Success\",\n" +
                "      \"ReturnCode\": 2,\n" +
                "      \"ReturnDescription\": \"Success\",\n" +
                "      \"ErrorDetail\": \"\",\n" +
                "      \"Customer\": {\n" +
                "        \"CustomerNo\": \"3544\",\n" +
                "        \"OneClickId\": \"\",\n" +
                "        \"Name\": \"HARI\",\n" +
                "        \"MidName\": \"\",\n" +
                "        \"Surname\": \"PRASAD\",\n" +
                "        \"BirthDate\": \"19890112\",\n" +
                "        \"FatherName\": \"\",\n" +
                "        \"MotherMaidenName\": \"testa\",\n" +
                "        \"Nationality\": \"IN\",\n" +
                "        \"PassportNo\": \"\",\n" +
                "        \"PassportIssuedBy\": \"\",\n" +
                "        \"PassportDateOfIssue\": \"19000101\",\n" +
                "        \"PassportDateOfExpire\": \"19000101\",\n" +
                "        \"PassportControlPeriod\": 0,\n" +
                "        \"EmergencyContactPersonNameSurname\": \"\",\n" +
                "        \"ResidenceCountryCode\": \"\",\n" +
                "        \"BirthCity\": \"\",\n" +
                "        \"BirthPlace\": \"\",\n" +
                "        \"Email\": \"ani@gmail.com\",\n" +
                "        \"CustomerType\": \"N\",\n" +
                "        \"Gender\": \"M\",\n" +
                "        \"CommunicationLanguage\": \"EN\",\n" +
                "        \"SendSMS\": \"\",\n" +
                "        \"SendEMail\": \"\",\n" +
                "        \"MobileNo\": \"919821001144\",\n" +
                "        \"WorkPlace\": \"\",\n" +
                "        \"Occupation\": \"Other\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode customerJsonNode = mapper.readTree(customerJson);
        when(transitCardClient.getCustomer(any(GetCustomer.class))).thenReturn(customerJsonNode);

        when(transitCardTxnService.verifyCardBin(any(String.class))).thenReturn(true);
        when(transitCardTxnService.isRegistrationAllowed(any(String.class))).thenReturn(true);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);

        when(paymentService.createCustomer(any(String.class), any(String.class))).thenReturn(customer);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(boolean.class))).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = authService.saveUser(authDTO);
        Assert.assertNotNull(authenticationDAOResult);
    }

    @Test
    public void saveUserNoCardIsCustomerTest() throws Exception{
        AuthDTO authDTO = new AuthDTO();
        authDTO.setDob(LocalDate.of(1995, 12, 24));
        authDTO.setCifId("884199663");
        authDTO.setPanNumber("GDOPS2323M");
        authDTO.setRegistrationType(RegistrationType.NoCardIsCustomer);

        Customer customer =   new Customer();
        customer.setFirstName("Pradeep");
        customer.setId("123");

        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");

        mockOccupations();

        String custDetail = "{\n" +
                "  \"getCustomerDtlsResponse\": {\n" +
                "    \"recordCount\": 8,\n" +
                "    \"matchFound\": \"True\",\n" +
                "    \"CustomerDetails\": {\n" +
                "      \"name\": \" KRAIVDN\",\n" +
                "      \"mobile\": \"9110000000110\",\n" +
                "      \"email\": \"nrivadaditi.k1@gmail.com\",\n" +
                "      \"aadharNo\": \"111111111001\",\n" +
                "      \"seedingStatus\": \"\",\n" +
                "      \"accountId\": \"\",\n" +
                "      \"identification\": \"111111111001\",\n" +
                "      \"aadhaar_addr\": \"111111111001\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String panDetail = "{\n" +
                "  \"getEntityDocResponse\": {\n" +
                "    \"ResponseBody\": {\n" +
                "      \"matchFound\": \"TRUE\",\n" +
                "      \"getEntityDocRecord\": [\n" +
                "        {\n" +
                "          \"ORGKEY\": \"884199663\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"recordCount\": 1\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode custDetailNode = mapper.readTree(custDetail);
        JsonNode panDetailNode = mapper.readTree(panDetail);

        when(finacleClient.getCustomerDetails(any(GetCustomerDtlsRequest.class))).thenReturn(custDetailNode);
        when(finacleClient.getEntityDoc(any(GetEntityDocRequest.class))).thenReturn(panDetailNode);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);

        when(paymentService.createCustomer(any(String.class), any(String.class))).thenReturn(customer);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(boolean.class))).thenReturn(authenticationDAO);
        AuthenticationDAO authenticationDAOResult = authService.saveUser(authDTO);
        Assert.assertNotNull(authenticationDAOResult);
    }

    @Test
    public void processFullNameTestCase1() throws Exception{
        String fullName  = "Pradeep Kumar Sharma";
        authService.processFullName(fullName, daoUser);
        Assert.assertEquals("Kumar", daoUser.getMiddleName());
    }

    @Test
    public void processFullNameTestCase2() throws Exception{
        String fullName  = "Pradeep Kumar";
        authService.processFullName(fullName, daoUser);
        Assert.assertEquals("Pradeep", daoUser.getFirstName());
    }

    @Test
    public void processFullNameTestCase3() throws Exception{
        String fullName  = "Pradeep";
        authService.processFullName(fullName, daoUser);
        Assert.assertEquals("Pradeep", daoUser.getFirstName());
    }

    @Test
    public void enableOtpVericationTest() {
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        when(authenticationRepository.findByMobileAndIsActive(authenticationDAO.getMobile(), true)).thenReturn(authenticationDAO);
        authService.enableOtpVerication(authenticationDAO.getMobile(), CommonUtils.currentDateTime());
        Assert.assertEquals(true, authenticationDAO.getOtpVerification());
    }


    @Test
    public void setUserRoleTest() {
        Role role = new Role();
        role.setName(RoleConstants.USER_ROLE);
        when(roleRepository.findByNameAndIsActive(RoleConstants.USER_ROLE, true)).thenReturn(role);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        authService.setUserRole(authenticationDAO);
        Assert.assertNotNull(authenticationDAO.getRoles());
    }

    @Test
    public void saveRefreshTokenAndLastAccessTimeTest() {
        SessionDAO sessionDAO = new SessionDAO();
        when(sessionRepository.save(sessionDAO)).thenReturn(sessionDAO);
        when(authenticationRepository.findByUserNameIgnoreCaseAndIsActive("2233776633", true)).thenReturn(authenticationDAO);
        authService.saveRefreshTokenAndLastAccessTime(null, "2233776633");
        Assert.assertNotNull(authenticationDAO.getSessionDAO());
    }

    @Test
    public void setMpinTest() throws Exception {
        SetMpinDTO setMpinDTO = new SetMpinDTO();
        setMpinDTO.setMpin("232323");
        setMpinDTO.setConfirmMpin("232323");

        List<MpinLog> recentMpinLogs = new ArrayList<>();
        MpinLog mpinLog = new MpinLog();
        mpinLog.setMpin("$2a$10$xPtEx3KeGY8PvCOUYyq8Ie55FSdyERyQ2Kz4g8v/dHELZx9M51/E2");
        recentMpinLogs.add(mpinLog);

        when(mpinRepository.findAllByAuthenticationDAO_Id(any(Long.class), any(Pageable.class))).thenReturn(recentMpinLogs);

        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.setUserAttempts(4);
        sessionDAO.setBlocked(true);
        LocalDateTime blockTime = CommonUtils.currentDateTime().minus(Duration.of(10, ChronoUnit.MINUTES));
        sessionDAO.setBlockTime(blockTime);
        authenticationDAO.setSessionDAO(sessionDAO);

        PowerMockito.mockStatic(BCrypt.class);
        PowerMockito.when(BCrypt.checkpw(any(String.class), any(String.class))).thenReturn(true);
        when(sessionRepository.save(any(SessionDAO.class))).thenReturn(sessionDAO);
        when(redisClient.getValue(any(String.class))).thenReturn("abc");
        PowerMockito.mockStatic(EncryptionUtil.class);
        PowerMockito.when(EncryptionUtil.decryptText("232323", "abc")).thenReturn("232323");
        doNothing().when(redisClient).deleteKey(any(String.class));
        try {
            authService.setMpin(setMpinDTO);
        } catch (Exception exception) {
            Assert.assertEquals("New MPIN should not be same as last 3 MPINs", exception.getMessage());
        }
    }

    @Test
    public void confirmCiFIdTest() throws Exception {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setCifId("2356341188");
        authDTO.setPanNumber("GDOPS2323M");
        String custDetail = "{\n" +
                "  \"getCustomerDtlsResponse\": {\n" +
                "    \"recordCount\": 8,\n" +
                "    \"matchFound\": \"True\",\n" +
                "    \"CustomerDetails\": {\n" +
                "      \"name\": \" KRAIVDN\",\n" +
                "      \"mobile\": \"9110000000110\",\n" +
                "      \"email\": \"nrivadaditi.k1@gmail.com\",\n" +
                "      \"aadharNo\": \"111111111001\",\n" +
                "      \"seedingStatus\": \"\",\n" +
                "      \"accountId\": \"\",\n" +
                "      \"identification\": \"111111111001\",\n" +
                "      \"aadhaar_addr\": \"111111111001\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String panDetail = "{\n" +
                "  \"getEntityDocResponse\": {\n" +
                "    \"ResponseBody\": {\n" +
                "      \"matchFound\": \"TRUE\",\n" +
                "      \"getEntityDocRecord\": [\n" +
                "        {\n" +
                "          \"ORGKEY\": \"884199663\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"recordCount\": 1\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode custDetailNode = mapper.readTree(custDetail);
        JsonNode panDetailNode = mapper.readTree(panDetail);

        when(finacleClient.getCustomerDetails(any(GetCustomerDtlsRequest.class))).thenReturn(custDetailNode);
        when(finacleClient.getEntityDoc(any(GetEntityDocRequest.class))).thenReturn(panDetailNode);

        Boolean result = authService.confirmCifId(authDTO);
        Assert.assertEquals(false, result);

    }

    @Test
    public void checkMpinTest() throws Exception {
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.setUserAttempts(6);

        LocalDateTime blockTime = CommonUtils.currentDateTime().minus(Duration.of(10, ChronoUnit.MINUTES));
        sessionDAO.setBlockTime(blockTime);
        authenticationDAO.setSessionDAO(sessionDAO);

        PowerMockito.mockStatic(BCrypt.class);
        PowerMockito.when(BCrypt.checkpw(any(String.class), any(String.class))).thenReturn(true);

        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        when(redisClient.getValue(any(String.class))).thenReturn("abc");
        PowerMockito.mockStatic(EncryptionUtil.class);
        PowerMockito.when(EncryptionUtil.decryptText("232323", "abc")).thenReturn("232323");
        doNothing().when(redisClient).deleteKey(any(String.class));
        try {
            authService.checkMpin("pradeep", "232323");
        } catch (Exception exception) {
            Assert.assertEquals("Your account is blocked, Please retry after 5 minutes.", exception.getMessage());
        }
    }

    @Test
    public void checkMpinTest2() throws Exception {
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.setUserAttempts(4);
        sessionDAO.setBlocked(true);
        LocalDateTime blockTime = CommonUtils.currentDateTime().minus(Duration.of(10, ChronoUnit.MINUTES));
        sessionDAO.setBlockTime(blockTime);
        authenticationDAO.setSessionDAO(sessionDAO);

        PowerMockito.mockStatic(BCrypt.class);
        PowerMockito.when(BCrypt.checkpw(any(String.class), any(String.class))).thenReturn(true);

        when(authenticationRepository.findByMobileAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        when(redisClient.getValue(any(String.class))).thenReturn("abc");
        PowerMockito.mockStatic(EncryptionUtil.class);
        PowerMockito.when(EncryptionUtil.decryptText("232323", "abc")).thenReturn("232323");
        doNothing().when(redisClient).deleteKey(any(String.class));
        try {
            authService.checkMpin("pradeep", "232323");
        } catch (Exception exception) {
            Assert.assertEquals("Invalid MPIN", exception.getMessage());
        }
    }

    @Test
    public void checkRefreshToken() throws Exception {
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.setRefreshToken("ygui1u1u");
        authenticationDAO.setSessionDAO(sessionDAO);
        when(authenticationRepository.findByUserNameIgnoreCaseAndIsActive(any(String.class), any(Boolean.class))).thenReturn(authenticationDAO);
        authService.checkRefreshToken("ygui1u1u", "pradeep");
    }

}