package com.axisbank.transit.explore.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DAO.AddressDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.repository.AddressRepository;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilterDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import com.axisbank.transit.explore.model.request.AddressDTO;
import com.axisbank.transit.explore.model.request.ExploreNotificationDTO;
import com.axisbank.transit.explore.model.request.ExploreStatusDTO;
import com.axisbank.transit.explore.model.request.SlotDTO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.repository.SlotRepository;
import com.axisbank.transit.explore.service.impl.ExploreServiceImpl;
import com.axisbank.transit.explore.shared.constants.ExploreStatus;
import com.axisbank.transit.explore.util.ChangeExploreStatusUtil;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.*;
import static com.axisbank.transit.explore.constants.LevelOneFilters.*;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class ExploreServiceTest extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    private ExploreDAO exploreDAO;
    private Set<SlotDAO> slotDAOSet;
    private AddressDAO addressDAO;
    private TargetOptionDTO filter1;
    private TargetOptionDTO filter2;
    private TargetOptionDTO filter3;
    private TargetOptionDTO filter4;

    @Mock
    ExploreRepository exploreRepository;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserUtil userUtil;

    @Mock
    ChangeExploreStatusUtil changeExploreStatusUtil;

    @Mock
    AddressRepository addressRepository;

    @Mock
    SlotRepository slotRepository;

    @Mock
    GlobalConfigService globalConfigService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    @Autowired
    ExploreServiceImpl exploreService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();

        authenticationDAO.setMobile("1122334455");
        authenticationDAO.setUserName("1122334455");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setRoles(new ArrayList<>());

        daoUser = new DAOUser();
        daoUser.setOccupation("Other");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);
        authenticationDAO.setDaoUser(daoUser);

        exploreDAO = new ExploreDAO();
        exploreDAO.setExploreId("112233");
        exploreDAO.setName("Flipkart");
        exploreDAO.setExploreType("offer");
        exploreDAO.setCurrentStatus("published");
        exploreDAO.setDescription("description");

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
        ExploreFilterDTO  customizedTargettingFilter = null;

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

        customizedTargettingFilter = new ExploreFilterDTO();
        customizedTargettingFilter.setName(FILE_UPLOAD_DATA);
        customizedTargettingFilter.setType("single-select-upload");
        customizedTargettingFilter.setSelectedVal("DoB");
        customizedTargettingFilter.setListVals(Arrays.asList("23/11/1994"));


        List<ExploreFilterDTO> subFilters = new ArrayList<>();
        subFilters.add(ageFilter);
        subFilters.add(genderFilter);
        subFilters.add(occupationFilter);

        filter1 = new TargetOptionDTO();
        filter1.setFilter(ALL_KOCHI1_CARD_USERS);
        filter1.setSubFilters(subFilters);

        filter2 = new TargetOptionDTO();
        filter2.setFilter(APP_ONLY_USERS);
        filter2.setSubFilters(subFilters);

        filter3 = new TargetOptionDTO();
        filter3.setFilter(ALL_USERS);
        filter3.setSubFilters(subFilters);

        filter4 = new TargetOptionDTO();
        filter4.setFilter(CUSTOMISED_TARGETTING);
        List<ExploreFilterDTO> subFiltersForCustomizedTargetting = new ArrayList<>();
        subFiltersForCustomizedTargetting.add(customizedTargettingFilter);
        filter4.setSubFilters(subFiltersForCustomizedTargetting);

    }

    @Test
    public void mapExploreItemsTest() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter3);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        when(exploreRepository.findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(any(String.class), any(Boolean.class))).thenReturn(exploreDAOList);

        when(exploreRepository.saveAll(any(Collection.class))).thenReturn(exploreDAOList);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);

        exploreService.mapExploreItems(authenticationDAO);
        Assert.assertEquals(false, authenticationDAO.getExploreDAOSet().isEmpty());
    }

    @Test
    public void mapExploreItemsTest1() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter1);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        when(exploreRepository.findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(any(String.class), any(Boolean.class))).thenReturn(exploreDAOList);

        when(exploreRepository.saveAll(any(Collection.class))).thenReturn(exploreDAOList);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);

        exploreService.mapExploreItems(authenticationDAO);
        Assert.assertEquals(true, authenticationDAO.getExploreDAOSet().isEmpty());
    }

    @Test
    public void mapExploreItemsTest2() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter2);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        when(exploreRepository.findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(any(String.class), any(Boolean.class))).thenReturn(exploreDAOList);

        when(exploreRepository.saveAll(any(Collection.class))).thenReturn(exploreDAOList);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);

        exploreService.mapExploreItems(authenticationDAO);
        Assert.assertEquals(false, authenticationDAO.getExploreDAOSet().isEmpty());
    }

    @Test
    public void mapExploreItemsTest3() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter4);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        when(exploreRepository.findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(any(String.class), any(Boolean.class))).thenReturn(exploreDAOList);

        when(exploreRepository.saveAll(any(Collection.class))).thenReturn(exploreDAOList);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);
        List<DAOUser> daoUserList = new ArrayList<>();
        daoUserList.add(daoUser);
        when(userRepository.findByDobIn(anyList())).thenReturn(daoUserList);
        exploreService.mapExploreItems(authenticationDAO);
        Assert.assertEquals(false, authenticationDAO.getExploreDAOSet().isEmpty());
    }

    @Test
    public void updateCurrentExploreStatusAndUpdatedByTest1() throws Exception {
        ExploreStatusDTO exploreStatusDTO = new ExploreStatusDTO();
        exploreStatusDTO.setStatus(ExploreStatus.CREATED);
        exploreStatusDTO.setExploreId("12345");
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(changeExploreStatusUtil.validateStatusChange(any(String.class), any(String.class))).thenReturn(true);
        when(changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO)).thenReturn(exploreDAO);
        when(exploreRepository.save(exploreDAO)).thenReturn(exploreDAO);
        when(exploreRepository.findByExploreId(exploreStatusDTO.getExploreId())).thenReturn(exploreDAO);
        Assert.assertNotNull(exploreService.updateCurrentExploreStatusAndUpdatedBy(exploreStatusDTO));
    }

    @Test
    public void updateCurrentExploreStatusAndUpdatedByTest2() throws Exception {
        ExploreStatusDTO exploreStatusDTO = new ExploreStatusDTO();
        exploreStatusDTO.setStatus(ExploreStatus.APPROVED);
        exploreStatusDTO.setExploreId("12345");
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(changeExploreStatusUtil.validateStatusChange(any(String.class), any(String.class))).thenReturn(true);
        when(changeExploreStatusUtil.changeStatusToApproved(exploreDAO, authenticationDAO)).thenReturn(exploreDAO);
        when(exploreRepository.save(exploreDAO)).thenReturn(exploreDAO);
        when(exploreRepository.findByExploreId(exploreStatusDTO.getExploreId())).thenReturn(exploreDAO);
        Assert.assertNotNull(exploreService.updateCurrentExploreStatusAndUpdatedBy(exploreStatusDTO));
    }

    @Test
    public void updateCurrentExploreStatusAndUpdatedByTest3() throws Exception {
        ExploreStatusDTO exploreStatusDTO = new ExploreStatusDTO();
        exploreStatusDTO.setStatus(ExploreStatus.PUBLISHED);
        exploreStatusDTO.setExploreId("12345");
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(changeExploreStatusUtil.validateStatusChange(any(String.class), any(String.class))).thenReturn(true);
        when(changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO)).thenReturn(exploreDAO);
        when(exploreRepository.save(exploreDAO)).thenReturn(exploreDAO);
        when(exploreRepository.findByExploreId(exploreStatusDTO.getExploreId())).thenReturn(exploreDAO);
        Assert.assertNotNull(exploreService.updateCurrentExploreStatusAndUpdatedBy(exploreStatusDTO));
    }

    @Test
    public void updateCurrentExploreStatusAndUpdatedByTes4t() throws Exception {
        ExploreStatusDTO exploreStatusDTO = new ExploreStatusDTO();
        exploreStatusDTO.setStatus(ExploreStatus.REJECTED);
        exploreStatusDTO.setExploreId("12345");
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(changeExploreStatusUtil.validateStatusChange(any(String.class), any(String.class))).thenReturn(true);
        when(changeExploreStatusUtil.changeStatusToRejected(any(ExploreDAO.class), any(AuthenticationDAO.class))).thenReturn(exploreDAO);
        when(exploreRepository.save(exploreDAO)).thenReturn(exploreDAO);
        when(exploreRepository.findByExploreId(any(String.class))).thenReturn(exploreDAO);
        Assert.assertNotNull(exploreService.updateCurrentExploreStatusAndUpdatedBy(exploreStatusDTO));
    }

    @Test
    public void createNotificationTest() throws Exception {
        ExploreNotificationDTO exploreNotificationDTO = new ExploreNotificationDTO();
        exploreNotificationDTO.setExploreId("explore123");
        exploreNotificationDTO.setExploreType("offer");
        exploreNotificationDTO.setName("Flipkart");
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressType("home");
        addressDTO.setCity("bangalore");
        exploreNotificationDTO.setAddress(addressDTO);
        List<SlotDTO> slotDTOList = new ArrayList<>();
        SlotDTO slotDTO = new SlotDTO();
        slotDTO.setStartDate(LocalDate.now());
        slotDTO.setStartTime("02:30 PM");
        slotDTO.setEndDate(LocalDate.now().plusYears(100));
        slotDTO.setEndTime("02:30 PM");
        slotDTOList.add(slotDTO);
        exploreNotificationDTO.setSlot(slotDTOList);

        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(addressRepository.save(addressDAO)).thenReturn(addressDAO);
        doNothing().when(slotRepository).deleteAll();
        when(changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO)).thenReturn(exploreDAO);
        when(exploreRepository.save(any(ExploreDAO.class))).thenReturn(exploreDAO);
        exploreService.createExploreNotification(exploreNotificationDTO);
        Assert.assertEquals("Flipkart", exploreDAO.getName());
    }

    @Test
    public void updateExploreTest() throws Exception {
        ExploreNotificationDTO exploreNotificationDTO = new ExploreNotificationDTO();
        exploreNotificationDTO.setExploreId("explore123");
        exploreNotificationDTO.setExploreType("offer");
        exploreNotificationDTO.setName("Testing");
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressType("home");
        addressDTO.setCity("bangalore");
        exploreNotificationDTO.setAddress(addressDTO);
        List<SlotDTO> slotDTOList = new ArrayList<>();
        SlotDTO slotDTO = new SlotDTO();
        slotDTO.setStartDate(LocalDate.now());
        slotDTO.setStartTime("02:30 PM");
        slotDTO.setEndDate(LocalDate.now().plusYears(100));
        slotDTO.setEndTime("02:30 PM");
        slotDTOList.add(slotDTO);
        exploreNotificationDTO.setSlot(slotDTOList);

        exploreDAO.setCurrentStatus(ExploreStatus.CREATED);

        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(addressRepository.save(addressDAO)).thenReturn(addressDAO);
        doNothing().when(slotRepository).deleteAll();
        when(changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO)).thenReturn(exploreDAO);
        when(exploreRepository.save(any(ExploreDAO.class))).thenReturn(exploreDAO);
        when(exploreRepository.findByExploreId(any(String.class))).thenReturn(exploreDAO);
        exploreService.updateExplore(exploreNotificationDTO);
        Assert.assertEquals("Testing", exploreDAO.getName());
    }

    @Test
    public void getAllNotificationsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(exploreRepository.findAllExploreIdsByLatLongCategorySubTypeAndExploreTypeAndCurStatus(any(Double.class),
                any(Double.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Arrays.asList("1234"));
        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);
        when(exploreRepository.findByExploreIdInAndAuthenticationDAOSet(Arrays.asList("1234"), authenticationDAO)).thenReturn(exploreDAOList);
        when(exploreRepository.findAllByCategoryLikeAndSubTypeLikeAndExploreTypeLikeAndCurrentStatusAndAuthenticationDAOSetOrderByUpdatedAtDesc(any(String.class),
                any(String.class), any(String.class), any(String.class), any(AuthenticationDAO.class))).thenReturn(exploreDAOList);

        Assert.assertNotNull(exploreService.getAllNotifications("Jwellery", "", "offer", 23.4, 34.5));
    }

    @Test
    public void getExploreDetailsTest() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter3);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);
        authenticationDAO.getExploreDAOSet().add(exploreDAO);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(exploreRepository.findByExploreIdAndIsActive(any(String.class), any(Boolean.class))).thenReturn(exploreDAO);
        Assert.assertNotNull(exploreService.getExploreDetails("123",23.4, 34.5));
    }

    @Test
    public void getAllExploreTest() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter3);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);
        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(exploreRepository.findAllByExploreTypeLike(any(String.class), any(Pageable.class))).thenReturn(exploreDAOList);
        Assert.assertNotNull(exploreService.getAllExplore(0, 10, "offer"));
    }

    @Test
    public void deleteExploreTest() throws Exception {
        when(exploreRepository.findByExploreId(any(String.class))).thenReturn(exploreDAO);
        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);
        List<AuthenticationDAO> authenticationDAOList = new ArrayList<>();
        when(authenticationRepository.saveAll(exploreDAO.getAuthenticationDAOSet())).thenReturn(authenticationDAOList);
        doNothing().when(exploreRepository).deleteByExploreId(any(String.class));
        exploreService.deleteExplore("123");
    }

    @Test
    public void getTargetAudienceTest() throws Exception {
        GlobalConfigDTO occupationGlobalConfigDTO = new GlobalConfigDTO();
        occupationGlobalConfigDTO.setJson(true);
        occupationGlobalConfigDTO.setValue(null);
        occupationGlobalConfigDTO.setKey(VALID_OCCUPATIONS);
        String ocuupationJson = "{\n" +
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
        JsonNode occupationJsonNode = mapper.readTree(ocuupationJson);
        occupationGlobalConfigDTO.setJsonValue(occupationJsonNode);
        when(globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true)).thenReturn(occupationGlobalConfigDTO);

        GlobalConfigDTO ageGlobalConfigDTO = new GlobalConfigDTO();
        ageGlobalConfigDTO.setJson(true);
        ageGlobalConfigDTO.setValue(null);
        ageGlobalConfigDTO.setKey(VALID_AGE_RANGE);
        String ageJson = "{\n" +
                "                \"maxVal\": \"70\",\n" +
                "                \"minVal\": \"10\"\n" +
                "            }";
        JsonNode ageJsonNode = mapper.readTree(ageJson);
        ageGlobalConfigDTO.setJsonValue(ageJsonNode);
        when(globalConfigService.getGlobalConfig(VALID_AGE_RANGE, true)).thenReturn(ageGlobalConfigDTO);

        GlobalConfigDTO genderGlobalConfigDTO = new GlobalConfigDTO();
        genderGlobalConfigDTO.setJson(true);
        genderGlobalConfigDTO.setValue(null);
        genderGlobalConfigDTO.setKey(VALID_GENDERS);
        String genderJson = "{\n" +
                "                \"genders\": [\n" +
                "                    \"MALE\",\n" +
                "                    \"FEMALE\",\n" +
                "                    \"TRANSGENDER\"\n" +
                "                ]\n" +
                "            }";
        JsonNode genderJsonNode = mapper.readTree(genderJson);
        genderGlobalConfigDTO.setJsonValue(genderJsonNode);
        when(globalConfigService.getGlobalConfig(VALID_GENDERS, true)).thenReturn(genderGlobalConfigDTO);

        GlobalConfigDTO customizedGlobalConfigDTO = new GlobalConfigDTO();
        customizedGlobalConfigDTO.setJson(true);
        customizedGlobalConfigDTO.setValue(null);
        customizedGlobalConfigDTO.setKey(VALID_CUSTOMIZED_TARGETTING_NAMES);
        String customizedJson = "{\n" +
                "                \"targettingVals\": [\n" +
                "                    \"Transit App ID\",\n" +
                "                    \"Mobile Number\",\n" +
                "                    \"DoB\"\n" +
                "                ]\n" +
                "            }";
        JsonNode customizedJsonNode = mapper.readTree(customizedJson);
        customizedGlobalConfigDTO.setJsonValue(customizedJsonNode);
        when(globalConfigService.getGlobalConfig(VALID_CUSTOMIZED_TARGETTING_NAMES, true)).thenReturn(customizedGlobalConfigDTO);

        Assert.assertNotNull(exploreService.getTargetAudience());
    }

    @Test
    public void getExploreFiltersTest() throws Exception {
        GlobalConfigDTO offerCategoriesGlobalConfigDTO = new GlobalConfigDTO();
        offerCategoriesGlobalConfigDTO.setJson(true);
        offerCategoriesGlobalConfigDTO.setValue(null);
        offerCategoriesGlobalConfigDTO.setKey(OFFER_CATEGORIES);
        String offerCategoryJson = "{\n" +
                "                \"categories\": [\n" +
                "                    \"Dining\",\n" +
                "                    \"Education\",\n" +
                "                    \"Entertainment\",\n" +
                "                    \"Food Delivery\",\n" +
                "                    \"Grocery\",\n" +
                "                    \"Hotel\",\n" +
                "                    \"Jewellery\",\n" +
                "                    \"Online Doctor Consultation\",\n" +
                "                    \"Others\",\n" +
                "                    \"Pharmacy\",\n" +
                "                    \"Retail \",\n" +
                "                    \"Services\",\n" +
                "                    \"Shopping\",\n" +
                "                    \"Spa & Wellness\",\n" +
                "                    \"Travel\",\n" +
                "                    \"Wellness\"\n" +
                "                ]\n" +
                "            }";
        JsonNode offerCategoryJsonNode = mapper.readTree(offerCategoryJson);
        offerCategoriesGlobalConfigDTO.setJsonValue(offerCategoryJsonNode);
        when(globalConfigService.getGlobalConfig(OFFER_CATEGORIES, true)).thenReturn(offerCategoriesGlobalConfigDTO);
        Assert.assertNotNull(exploreService.getExploreFilters());
    }

    @Test
    public void pushNotificationsTest1() throws Exception {
        doNothing().when(notificationService).saveNotification(anyList(), any(AddNotificationDTO.class));
        exploreDAO.getAuthenticationDAOSet().add(authenticationDAO);
        exploreDAO.setExploreType("NOTIFICATION");
        exploreService.pushNotifications(exploreDAO);
    }

    @Test
    public void pushNotificationsTest2() throws Exception {
        doNothing().when(notificationService).saveNotification(anyList(), any(AddNotificationDTO.class));
        exploreDAO.getAuthenticationDAOSet().add(authenticationDAO);
        exploreDAO.setExploreType("OFFER");
        exploreService.pushNotifications(exploreDAO);
    }

    @Test
    public void pushNotificationsTest3() throws Exception {
        doNothing().when(notificationService).saveNotification(anyList(), any(AddNotificationDTO.class));
        exploreDAO.getAuthenticationDAOSet().add(authenticationDAO);
        exploreDAO.setExploreType("PROMOTIONAL-OFFER");
        exploreService.pushNotifications(exploreDAO);
    }

    @Test
    public void getNearByExploerRadiusGCTest() throws Exception {
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(false);
        globalConfigDTO.setValue("2000");
        globalConfigDTO.setKey(NEARBY_EXPLORE_RADIUS);

        when(globalConfigService.getGlobalConfig(NEARBY_EXPLORE_RADIUS, true)).thenReturn(globalConfigDTO);
        Assert.assertNotNull(exploreService.getNearByExploerRadiusGC());
    }
}
