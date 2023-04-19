package com.axisbank.transit.core.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.MpinLog;
import com.axisbank.transit.core.model.DAO.AddressDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.service.impl.DashboardServiceImpl;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilterDTO;
import com.axisbank.transit.explore.model.DTO.MiscDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.MIN_BALANCE_NOTIFICATION;
import static com.axisbank.transit.explore.constants.LevelOneFilters.*;
import static com.axisbank.transit.explore.constants.LevelOneFilters.CUSTOMISED_TARGETTING;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.*;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.FILE_UPLOAD_DATA;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.PERMANENT_BLOCK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationSetupData.class, CommonUtils.class})
@PowerMockIgnore("javax.crypto.*")
public class DashboardServiceTest extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    private ExploreDAO exploreDAO;
    private Set<SlotDAO> slotDAOSet;
    private AddressDAO addressDAO;
    private TargetOptionDTO filter1;

    @Mock
    UserUtil userUtil;
    @Mock
    ExploreRepository exploreRepository;
    @Mock
    TransactionService transactionService;
    @Mock
    TransitCardTxnService transitCardTxnService;
    @Mock
    RedisClient redisClient;
    @Mock
    GlobalConfigService globalConfigService;
    @Mock
    NotificationService notificationService;
    @Mock
    ExploreService exploreService;
    @InjectMocks
    @Autowired
    DashboardServiceImpl dashboardService;

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

        exploreDAO = new ExploreDAO();
        exploreDAO.setExploreId("112233");
        exploreDAO.setName("Flipkart");
        exploreDAO.setExploreType("offer");
        exploreDAO.setCurrentStatus("published");
        exploreDAO.setDescription("description");
        exploreDAO.setAuthenticationDAO(authenticationDAO);
        exploreDAO.setCategory("xyz");
        exploreDAO.setBannerLink("");
        exploreDAO.setTitle("Flipkart");
        exploreDAO.setSubType("Online");
        exploreDAO.setDisclaimer("xyz");
        exploreDAO.setTicketLink("www.ticket.com");

        exploreDAO.setTermsAndConditions("abc");
        exploreDAO.setLogoLink("abc");
        exploreDAO.setWebsiteLink("www.google.com");

        slotDAOSet = new HashSet<>();
        SlotDAO slotDAO = new SlotDAO();
        slotDAO.setStartDate(LocalDate.now());
        slotDAO.setStartTime("02:30 PM");
        slotDAO.setEndDate(LocalDate.now().plusYears(100));
        slotDAO.setEndTime("02:30 PM");
        slotDAOSet.add(slotDAO);
        exploreDAO.setSlotDAOSet(slotDAOSet);

        addressDAO = new AddressDAO();
        addressDAO.setAddressId("address123");
        addressDAO.setCity("Bangalore");
        exploreDAO.setAddressDAO(addressDAO);

        ExploreFilterDTO ageFilter = null;
        ExploreFilterDTO  occupationFilter = null;
        ExploreFilterDTO  genderFilter = null;

        ageFilter = new ExploreFilterDTO();
        ageFilter.setName(AGE);
        ageFilter.setMaxVal(50.0);
        ageFilter.setMinVal(10.0);

        occupationFilter = new ExploreFilterDTO();
        occupationFilter.setName(OCCUPATION);
        occupationFilter.setListVals(Arrays.asList("Other", "Homemaker", "Bussiness Professional"));

        genderFilter = new ExploreFilterDTO();
        genderFilter.setName(GENDER);
        List<String> genders = Stream.of(Gender.values())
                .map(Gender::name)
                .collect(Collectors.toList());
        genderFilter.setListVals(genders);


        List<ExploreFilterDTO> subFilters = new ArrayList<>();
        subFilters.add(ageFilter);
        subFilters.add(genderFilter);
        subFilters.add(occupationFilter);

        filter1 = new TargetOptionDTO();
        filter1.setFilter(ALL_KOCHI1_CARD_USERS);
        filter1.setSubFilters(subFilters);

    }

    @Test
    public void getDasboardDetailsTest() throws Exception {
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(TargetAudienceDTO.class))).thenReturn("target");
        PowerMockito.when(CommonUtils.convertObjectToJsonString(any(MiscDTO.class))).thenReturn("misc");
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter1);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        MiscDTO miscDTO = new MiscDTO();
        miscDTO.setHomeScreenLink("www.hi.com");
        exploreDAO.setMisc(miscDTO);

        exploreDAO.setUpdatedAt(new Date());
        exploreDAO.setCreatedAt(new Date());

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");
        CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();
        cardDetailsDAO.setCardNo("12345678");
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
        when(exploreService.getNearByExploerRadiusGC()).thenReturn(2000.0);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        TransitCardInfoDTO transitCardInfoDTO = new TransitCardInfoDTO();
        transitCardInfoDTO.setCardStatCode("N");
        transitCardInfoDTO.setCardStatSubCode("N");
        transitCardInfoDTO.setCustomerNo("xyz");
        transitCardInfoDTO.setCardNo("12345678");
        transitCardInfoDTO.setTotalCardBalance("270");
        transitCardInfoDTO.setTotalChipBalance("120");
        transitCardInfoDTO.setTotalHostBalance("90");

        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(transitCardInfoDTO);
        when(transitCardTxnService.getBlockStatus(any(String.class), any(String.class))).thenReturn(PERMANENT_BLOCK);
        when(transitCardTxnService.linkReplacementCard(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(transitCardInfoDTO);

        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(MIN_BALANCE_NOTIFICATION);
        String minBalNotificationJson = "{\n" +
                "                \"minBalance\": \"100\",\n" +
                "                \"subtitle\": \"Your KMRL Axis Bank Kochi1 Card balance is low. Recharge your card for quick travel.\",\n" +
                "                \"action\": \"topUp\",\n" +
                "                \"title\": \"Your Kochi1 Card balance is low\"\n" +
                "            }";

        JsonNode jsonNode = mapper.readTree(minBalNotificationJson);
        globalConfigDTO.setJsonValue(jsonNode);
        when(globalConfigService.getGlobalConfig(MIN_BALANCE_NOTIFICATION, true)).thenReturn(globalConfigDTO);
        when(redisClient.getValue(any(String.class))).thenReturn(null);

        doNothing().when(notificationService).saveNotification(any(AddNotificationDTO.class));
        doNothing().when(redisClient).setValue(any(String.class),any(String.class),any(Long.class));
        PowerMockito.when(CommonUtils.maskString(any(String.class), any(Integer.class), any(Integer.class), any(Character.class))).thenReturn("22**2266655");

        when(exploreRepository.findAllByExploreTypeAndCurrentStatusAndAuthenticationDAOSet(any(String.class), any(String.class), any(AuthenticationDAO.class))).thenReturn(exploreDAOList);
        PowerMockito.when(CommonUtils.checkIfOfferExpired(any(LocalDate.class), any(String.class))).thenReturn(false);

        Assert.assertNotNull(dashboardService.getDasboardDetails());

    }
}
