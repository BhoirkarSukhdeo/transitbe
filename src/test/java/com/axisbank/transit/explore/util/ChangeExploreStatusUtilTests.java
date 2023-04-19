package com.axisbank.transit.explore.util;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DAO.AddressDAO;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.ExploreFilterDTO;
import com.axisbank.transit.explore.model.DTO.TargetAudienceDTO;
import com.axisbank.transit.explore.model.DTO.TargetOptionDTO;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.axisbank.transit.explore.constants.LevelOneFilters.*;
import static com.axisbank.transit.explore.constants.LevelOneFilters.CUSTOMISED_TARGETTING;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.*;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.FILE_UPLOAD_DATA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class ChangeExploreStatusUtilTests {
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

    @InjectMocks
    @Autowired
    ChangeExploreStatusUtil changeExploreStatusUtil;

    @Mock
    UserRepository userRepository;

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
    public void changeStatusTest() {
        boolean result = changeExploreStatusUtil.validateStatusChange("created", "approved");
        Assert.assertEquals(true, result);
    }

    @Test
    public void changeStatusToCreateTest() {
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToRejectedTest() throws Exception {
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToRejected(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToApprovedTest() throws Exception {
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToApproved(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToPublishedTest3() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter3);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        authenticationDAO.getExploreDAOSet().add(exploreDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        List<DAOUser> daoUserList = new ArrayList<>();
        daoUserList.add(daoUser);
        when(userRepository.findByDobIn(anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToPublishedTest1() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter1);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);
        CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
        authenticationDAO.getExploreDAOSet().add(exploreDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        List<DAOUser> daoUserList = new ArrayList<>();
        daoUserList.add(daoUser);
        when(userRepository.findByDobIn(anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToPublishedTest2() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter2);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        authenticationDAO.getExploreDAOSet().add(exploreDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        List<DAOUser> daoUserList = new ArrayList<>();
        daoUserList.add(daoUser);
        when(userRepository.findByDobIn(anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO));
    }

    @Test
    public void changeStatusToPublishedTest4() throws Exception {
        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter4);
        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);
        exploreDAO.setTargetAudience(targetAudienceDTO);

        authenticationDAO.getExploreDAOSet().add(exploreDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);

        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);

        List<DAOUser> daoUserList = new ArrayList<>();
        daoUserList.add(daoUser);
        when(userRepository.findByDobIn(anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationIn(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNotNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        when(userRepository.findByDobGreaterThanEqualAndDobLessThanEqualAndGenderInAndOccupationInAndAuthenticationDAO_CardDetailsDAOIsNull(any(LocalDate.class), any(LocalDate.class), anyList(), anyList())).thenReturn(daoUserList);
        Assert.assertNotNull(changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO));
    }
}
