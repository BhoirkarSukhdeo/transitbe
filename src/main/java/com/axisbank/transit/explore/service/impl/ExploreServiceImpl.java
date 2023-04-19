package com.axisbank.transit.explore.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DAO.AddressDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.AddNotificationDTO;
import com.axisbank.transit.core.repository.AddressRepository;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.service.NotificationService;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.model.DAO.SlotDAO;
import com.axisbank.transit.explore.model.DTO.*;
import com.axisbank.transit.explore.model.request.AddressDTO;
import com.axisbank.transit.explore.model.request.ExploreNotificationDTO;
import com.axisbank.transit.explore.model.request.ExploreStatusDTO;
import com.axisbank.transit.explore.model.request.SlotDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailAdminDTO;
import com.axisbank.transit.explore.model.response.ExploreDetailDTO;
import com.axisbank.transit.explore.model.response.ExploreListViewDTO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.repository.SlotRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.explore.shared.constants.ExploreStatus;
import com.axisbank.transit.explore.util.ChangeExploreStatusUtil;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.*;
import static com.axisbank.transit.explore.constants.CustomizedTargettingName.DOB;
import static com.axisbank.transit.explore.constants.LevelOneFilters.*;
import static com.axisbank.transit.explore.constants.LevelTwoFilters.*;
import static com.axisbank.transit.explore.shared.constants.ExploreConstants.INSTORE_SHOPPING;
import static com.axisbank.transit.explore.shared.constants.ExploreConstants.ONLINE_SHOPPING;
import static com.axisbank.transit.explore.shared.constants.ExploreStatus.PUBLISHED;
import static com.axisbank.transit.explore.shared.constants.ExploreTypes.*;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.TRANSIT_USER;
import static com.axisbank.transit.userDetails.constants.Gender.NA;

@Slf4j
@Service
public class ExploreServiceImpl implements ExploreService {
    private final ModelMapper modelMapper = new ModelMapper();
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ExploreRepository exploreRepository;

    @Autowired
    ChangeExploreStatusUtil changeExploreStatusUtil;

    @Autowired
    UserUtil userUtil;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    NotificationService notificationService;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    GlobalConfigService globalConfigService;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public ExploreDAO updateCurrentExploreStatusAndUpdatedBy(ExploreStatusDTO exploreStatusDTO) throws Exception{
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            ExploreDAO exploreDAO = exploreRepository.findByExploreId(exploreStatusDTO.getExploreId());
            if(changeExploreStatusUtil.validateStatusChange(exploreDAO.getCurrentStatus(), exploreStatusDTO.getStatus())) {
                log.info("Status Change permitted");
                switch (exploreStatusDTO.getStatus()) {
                    case ExploreStatus.CREATED:
                        exploreDAO = changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO);
                        break;
                    case ExploreStatus.REJECTED:
                        exploreDAO = changeExploreStatusUtil.changeStatusToRejected(exploreDAO, authenticationDAO);
                        break;
                    case ExploreStatus.APPROVED:
                        exploreDAO = changeExploreStatusUtil.changeStatusToApproved(exploreDAO, authenticationDAO);
                        break;
                    case PUBLISHED:
                        exploreDAO = changeExploreStatusUtil.changeStatusToPublished(exploreDAO, authenticationDAO);
                        break;
                }
                exploreRepository.save(exploreDAO);
                return exploreDAO;
            }
        } catch (Exception err) {
            log.error("Unable to update the status: "+ err.getMessage());
            throw err;
        }
        return null;
    }

    @Transactional
    public void createExploreNotification(ExploreNotificationDTO exploreNotificationDTO) throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        Set<ExploreDAO> exploreDAOList = authenticationDAO.getExploreDAOS();
        try {
            if (exploreNotificationDTO != null ) {
                ExploreDAO exploreDAO  = new ExploreDAO();
                String exploreId = CommonUtils.generateRandomString(30);
                exploreDAO.setExploreId(exploreId);
                changeExploreStatusUtil.changeStatusToCreate(exploreDAO, authenticationDAO);
                setExploreDataAndSave(exploreDAO, exploreNotificationDTO);
            }
        } catch (Exception exception) {
            log.error("Exception in createExploreNotification: {}", exception.getMessage());
            throw new Exception("Exception in saving notification: "+exception.getLocalizedMessage());
        }
    }

    private void setExploreDataAndSave(ExploreDAO exploreDAO, ExploreNotificationDTO exploreNotificationDTO) throws Exception{
        exploreDAO.setExploreType(exploreNotificationDTO.getExploreType());
        exploreDAO.setName(exploreNotificationDTO.getName());
        exploreDAO.setTitle(exploreNotificationDTO.getTitle());
        exploreDAO.setSubType(exploreNotificationDTO.getSubType());
        exploreDAO.setCategory(exploreNotificationDTO.getCategory());
        exploreDAO.setDescription(exploreNotificationDTO.getDescription());
        exploreDAO.setDisclaimer(exploreNotificationDTO.getDisclaimer());
        exploreDAO.setLogoLink(exploreNotificationDTO.getLogoLink());
        exploreDAO.setBannerLink(exploreNotificationDTO.getBannerLink());
        exploreDAO.setWebsiteLink(exploreNotificationDTO.getWebsiteLink());
        exploreDAO.setMisc(exploreNotificationDTO.getMisc());
        exploreDAO.setTargetAudience(exploreNotificationDTO.getTargetAudience());
        exploreDAO.setTermsAndConditions(exploreNotificationDTO.getTermsAndConditions());

        if (exploreNotificationDTO.getAddress() != null ) {
            AddressDTO addressDTO = exploreNotificationDTO.getAddress();
            AddressDAO addressDAO = new AddressDAO();
            addressDAO.setAddressId(CommonUtils.generateRandomString(30));
            addressDAO.setLine1(addressDTO.getLine1());
            addressDAO.setLine2(addressDTO.getLine2());
            addressDAO.setAddressType(addressDTO.getAddressType());
            addressDAO.setLink(addressDTO.getLink());
            addressDAO.setCity(addressDTO.getCity());
            addressDAO.setDistrict(addressDTO.getDistrict());
            addressDAO.setPincode(addressDTO.getPincode());
            addressDAO.setState(addressDTO.getState());
            addressDAO.setLatitude(addressDTO.getLatitude());
            addressDAO.setLongitude(addressDTO.getLongitude());
            addressRepository.save(addressDAO);
            exploreDAO.setAddressDAO(addressDAO);
        }

        if (exploreNotificationDTO.getSlot() != null) {
            List<SlotDTO> slotDTOSet = exploreNotificationDTO.getSlot();
            if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                slotRepository.deleteAll(exploreDAO.getSlotDAOSet());
            }
            HashSet<SlotDAO> slotDAOSet = new HashSet<>();
            for (SlotDTO slotDTO : slotDTOSet) {
                SlotDAO slotDAO = new SlotDAO();
                slotDAO.setStartDate(slotDTO.getStartDate());
                slotDAO.setEndDate(slotDTO.getEndDate());
                slotDAO.setStartTime(slotDTO.getStartTime());
                slotDAO.setEndTime(slotDTO.getEndTime());
                slotDAO.setExploreDAO(exploreDAO);

                slotDAOSet.add(slotDAO);
            }
            exploreDAO.setSlotDAOSet(slotDAOSet);
        }
        exploreRepository.save(exploreDAO);
    }

    @Transactional
    public List<ExploreListViewDTO> getAllNotifications(String category, String subType, String exploreType, double latitude, double longitude) throws Exception {

        List<ExploreListViewDTO> result = new ArrayList<>();
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        try {
            String radius = "distance="+getNearByExploerRadiusGC();
            List<String> exploreIds = exploreRepository.findAllExploreIdsByLatLongCategorySubTypeAndExploreTypeAndCurStatus(latitude,
                    longitude, radius, category, subType, exploreType, "published");
            if (exploreIds != null && !exploreIds.isEmpty()) {
                List<ExploreDAO> exploreDAOList = exploreRepository.findByExploreIdInAndAuthenticationDAOSet(exploreIds, authenticationDAO);
                for (ExploreDAO exploreDAO: exploreDAOList) {
                    if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                        SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
                        for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                            sortedSlotDAO.add(slotDAO);
                        }
                        LocalDate endDate = sortedSlotDAO.last().getEndDate();
                        String endTime = sortedSlotDAO.last().getEndTime();
                        if (CommonUtils.checkIfOfferExpired(endDate, endTime)) {
                            continue;
                        }
                    }
                    ExploreListViewDTO exploreListViewDTO = modelMapper.map(exploreDAO, ExploreListViewDTO.class);
                    result.add(exploreListViewDTO);
                }
            }
            if (subType.equals(ONLINE_SHOPPING) || subType.isEmpty() || subType.equalsIgnoreCase("%")) {
                List<ExploreDAO> exploreDAOListOnline = exploreRepository.findAllByCategoryLikeAndSubTypeLikeAndExploreTypeLikeAndCurrentStatusAndAuthenticationDAOSetOrderByUpdatedAtDesc(category,
                        ONLINE_SHOPPING, exploreType, "published", authenticationDAO);
                for (ExploreDAO exploreDAO : exploreDAOListOnline) {
                    if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                        SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
                        for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                            sortedSlotDAO.add(slotDAO);
                        }
                        LocalDate endDate = sortedSlotDAO.last().getEndDate();
                        String endTime = sortedSlotDAO.last().getEndTime();
                        try {
                            if (CommonUtils.checkIfOfferExpired(endDate, endTime)) {
                                continue;
                            }
                        } catch (Exception exception) {
                            log.error("Error in checking expiration");
                            continue;
                        }
                    }
                    ExploreListViewDTO exploreListViewDTO = modelMapper.map(exploreDAO, ExploreListViewDTO.class);
                    result.add(exploreListViewDTO);
                }
            }

        } catch (Exception exception) {
            log.error("Exception in fetching explore Data: {}", exception.getMessage());
            throw new Exception("Error in fetching explore data, Please try again later");
        }
        return result;
    }

    @Transactional
    public ExploreDetailDTO getExploreDetails(String exploreId, double latitude, double longitude) throws Exception {
        log.info("Request received in getExploreDetails method with exploreId: "+exploreId);
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        ExploreDetailDTO result = null;
        AddressDTO addressDTO = null;
        List<SlotDTO> slotDTOList = new ArrayList<>();
        String distance = "4.6 km"; // TODO: to be calculated using street map
        String time = "12 min"; // TODO: to be calculated using street map
        try {
            ExploreDAO exploreDAO = exploreRepository.findByExploreIdAndIsActive(exploreId, true);
            if (!authenticationDAO.getExploreDAOSet().contains(exploreDAO)) {
                throw new Exception("You do not have access to view this item");
            }
            if (exploreDAO.getAddressDAO() != null) {
                AddressDAO addressDAO = exploreDAO.getAddressDAO();
                addressDTO = new AddressDTO(
                        addressDAO.getLine1(),
                        addressDAO.getLine2(),
                        addressDAO.getAddressType(),
                        addressDAO.getCity(),
                        addressDAO.getDistrict(),
                        addressDAO.getState(),
                        addressDAO.getPincode(),
                        addressDAO.getLink(),
                        addressDAO.getLatitude(),
                        addressDAO.getLongitude()
                );
            }
            if (exploreDAO.getSlotDAOSet() != null && !(exploreDAO.getSlotDAOSet().isEmpty())) {
                Set<SlotDAO> slotDAOSet = exploreDAO.getSlotDAOSet();
                for (SlotDAO slotDAO : slotDAOSet) {
                    SlotDTO slotDTO = new SlotDTO(
                            slotDAO.getStartDate(),
                            slotDAO.getEndDate(),
                            slotDAO.getStartTime(),
                            slotDAO.getEndTime()
                    );
                    slotDTOList.add(slotDTO);
                }
            }

            TargetAudienceDTO targetAudienceDTO = null;
            try {
                targetAudienceDTO = exploreDAO.getTargetAudience();
            } catch (Exception exception) {
                log.error("target audience is null for exploreId: {}", exploreDAO.getExploreId());
            }
            result =  new ExploreDetailDTO(
                    exploreDAO.getName(),
                    exploreDAO.getTitle(),
                    exploreDAO.getSubType(),
                    exploreDAO.getDescription(),
                    slotDTOList,
                    addressDTO,
                    exploreDAO.getDisclaimer(),
                    exploreDAO.getTermsAndConditions(),
                    exploreDAO.getLogoLink(),
                    exploreDAO.getBannerLink(),
                    exploreDAO.getWebsiteLink(),
                    distance,
                    time,
                    targetAudienceDTO

            );
        } catch (Exception ex) {
            log.error("Error in getting Explore Details: {}", ex.getMessage());
            throw ex;
        }
        return result;
    }

    @Override
    @Transactional
    public List<ExploreDetailAdminDTO> getAllExplore(int page, int size, String exploreType) throws Exception{
        List<ExploreDetailAdminDTO> result = new ArrayList<>();
        Pageable requestedPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        List<ExploreDAO> exploreDAOList = exploreRepository.findAllByExploreTypeLike(exploreType, requestedPage);
        for (ExploreDAO exploreDAO: exploreDAOList){
            ExploreDetailAdminDTO exploreDetailAdminDTO = null;
            AddressDTO addressDTO = null;
            LocalDate startDate = null;
            LocalDate endDate = null;
            String startTime = null;
            String endTime = null;
            if (authenticationDAO.getRolesList().size() == 1 && authenticationDAO.getRolesList().contains(RoleConstants.PUBLISHER) && exploreDAO.getCurrentStatus().equalsIgnoreCase(ExploreStatus.CREATED)) {
                continue;
            }
            if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
                for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                    sortedSlotDAO.add(slotDAO);
                }
                startDate = sortedSlotDAO.first().getStartDate();
                endDate = sortedSlotDAO.last().getEndDate();
                startTime = sortedSlotDAO.first().getStartTime();
                endTime = sortedSlotDAO.last().getEndTime();
            }

            if (exploreDAO.getAddressDAO() != null) {
                AddressDAO addressDAO = exploreDAO.getAddressDAO();
                addressDTO = new AddressDTO(
                        addressDAO.getLine1(),
                        addressDAO.getLine2(),
                        addressDAO.getAddressType(),
                        addressDAO.getCity(),
                        addressDAO.getDistrict(),
                        addressDAO.getState(),
                        addressDAO.getPincode(),
                        addressDAO.getLink(),
                        addressDAO.getLatitude(),
                        addressDAO.getLongitude()
                );
            }
            MiscDTO misc = exploreDAO.getMisc();
            String homeScreenLink = "";
            if ( misc!=null){
                homeScreenLink = misc.getHomeScreenLink();
            }
            else {
                homeScreenLink = exploreDAO.getBannerLink();
            }
            TargetAudienceDTO targetAudienceDTO = null;

            try {
                targetAudienceDTO = exploreDAO.getTargetAudience();
            } catch (Exception exception) {
                log.error("target audience is null for exploreId: {}", exploreDAO.getExploreId());
            }

            exploreDetailAdminDTO = new ExploreDetailAdminDTO(
                    exploreDAO.getExploreId(),
                    exploreDAO.getName(),
                    exploreDAO.getTitle(),
                    exploreDAO.getSubType(),
                    exploreDAO.getDescription(),
                    addressDTO,
                    exploreDAO.getDisclaimer(),
                    exploreDAO.getTermsAndConditions(),
                    exploreDAO.getLogoLink(),
                    exploreDAO.getBannerLink(),
                    exploreDAO.getWebsiteLink(),
                    exploreDAO.getCurrentStatus(),
                    exploreDAO.getCategory(),
                    homeScreenLink,
                    startDate,
                    endDate,
                    startTime,
                    endTime,
                    targetAudienceDTO
            );
            result.add(exploreDetailAdminDTO);
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteExplore(String exploreId) throws Exception {
        log.info("Request received for deleting explore data");
        try {
            ExploreDAO exploreDAO = exploreRepository.findByExploreId(exploreId);
            exploreDAO.getAuthenticationDAOSet().forEach(u -> u.getExploreDAOSet().remove(exploreDAO));
            authenticationRepository.saveAll(exploreDAO.getAuthenticationDAOSet());
            exploreRepository.deleteByExploreId(exploreId);
        } catch (Exception exception) {
            log.error("Exception in deleting explore Data: {}", exception.getMessage());
            throw new Exception("Error in deletion, Please try again later.");
        }
    }

    @Override
    @Transactional
    public void updateExplore(ExploreNotificationDTO exploreNotificationDTO) throws Exception {
        log.info("Request received for updating explore data");
        try {
            ExploreDAO exploreDAO = exploreRepository.findByExploreId(exploreNotificationDTO.getExploreId());
            if (exploreDAO == null) {
                log.error("Explore Id not present in DB: ");
                throw new Exception("Please provide valid explore ID");
            }
            if (exploreDAO.getCurrentStatus().equalsIgnoreCase(PUBLISHED)) {
                throw new Exception("Published Item can't be edited");
            }
            setExploreDataAndSave(exploreDAO, exploreNotificationDTO);
        } catch (Exception exception) {
            log.error("Error in updateExplore: {}", exception.getMessage());
            throw new Exception("Error in updating Explore Data, Please try again later");
        }
    }

    @Override
    public TargetAudienceDTO getTargetAudience() throws Exception {
        log.info("request received in getTargetAudience method");
        ExploreFilterDTO  ageFilter = null;
        ExploreFilterDTO  occupationFilter = null;
        ExploreFilterDTO  genderFilter = null;
        ExploreFilterDTO  customizedTargettingFilter = null;
        GlobalConfigDTO ageGlobalConfig = globalConfigService.getGlobalConfig(VALID_AGE_RANGE, true);
        if (ageGlobalConfig != null) {
            JsonNode ageNode = ageGlobalConfig.getJsonValue();
            ageFilter = new ExploreFilterDTO();
            ageFilter.setName(AGE);
            ageFilter.setType("min-max");
            ageFilter.setValidMin(ageNode.get("minVal").asDouble());
            ageFilter.setValidMax(ageNode.get("maxVal").asDouble());
        }

        GlobalConfigDTO occupationGlobalConfig = globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true);
        if (occupationGlobalConfig != null) {
            JsonNode genderNode = occupationGlobalConfig.getJsonValue();
            List<String> occupations= mapper.convertValue(genderNode.get("occupations"), new TypeReference<List<String>>(){});
            occupationFilter = new ExploreFilterDTO();
            occupationFilter.setName(OCCUPATION);
            occupationFilter.setType("multi-select");
            occupationFilter.setValidVals(occupations);
        }

        GlobalConfigDTO gendersGlobalConfig = globalConfigService.getGlobalConfig(VALID_GENDERS, true);
        if (gendersGlobalConfig != null) {
            JsonNode genderNode = gendersGlobalConfig.getJsonValue();
            List<String> genders= mapper.convertValue(genderNode.get("genders"), new TypeReference<List<String>>(){});
            genderFilter = new ExploreFilterDTO();
            genderFilter.setName(GENDER);
            genderFilter.setType("multi-select");
            genderFilter.setValidVals(genders);
        }

        GlobalConfigDTO customizedTargettingGlobalConfig = globalConfigService.getGlobalConfig(VALID_CUSTOMIZED_TARGETTING_NAMES, true);
        if (customizedTargettingGlobalConfig != null) {
            JsonNode customizedTargettingNode = customizedTargettingGlobalConfig.getJsonValue();
            List<String> targettingVals= mapper.convertValue(customizedTargettingNode.get("targettingVals"), new TypeReference<List<String>>(){});
            customizedTargettingFilter = new ExploreFilterDTO();
            customizedTargettingFilter.setName(FILE_UPLOAD_DATA);
            customizedTargettingFilter.setType("single-select-upload");
            customizedTargettingFilter.setValidVals(targettingVals);
        }

        List<ExploreFilterDTO> subFilters = new ArrayList<>();
        subFilters.add(ageFilter);
        subFilters.add(genderFilter);
        subFilters.add(occupationFilter);

        TargetOptionDTO filter1 = new TargetOptionDTO();
        filter1.setFilter(ALL_KOCHI1_CARD_USERS);
        filter1.setSubFilters(subFilters);

        TargetOptionDTO filter2 = new TargetOptionDTO();
        filter2.setFilter(APP_ONLY_USERS);
        filter2.setSubFilters(subFilters);

        TargetOptionDTO filter3 = new TargetOptionDTO();
        filter3.setFilter(ALL_USERS);
        filter3.setSubFilters(subFilters);

        TargetOptionDTO filter4 = new TargetOptionDTO();
        filter4.setFilter(CUSTOMISED_TARGETTING);
        List<ExploreFilterDTO> subFiltersForCustomizedTargetting = new ArrayList<>();
        subFiltersForCustomizedTargetting.add(customizedTargettingFilter);
        filter4.setSubFilters(subFiltersForCustomizedTargetting);

        List<TargetOptionDTO> data = new ArrayList<>();
        data.add(filter1);
        data.add(filter2);
        data.add(filter3);
        data.add(filter4);

        TargetAudienceDTO targetAudienceDTO = new TargetAudienceDTO();
        targetAudienceDTO.setData(data);

        return targetAudienceDTO;
    }

    @Override
    public ExploreFilters getExploreFilters() throws Exception {
        log.info("Inside getOfferCategories method: ");
        ExploreFilters exploreFilters = new ExploreFilters();
        try {
            GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(OFFER_CATEGORIES, true);
            if (globalConfig != null) {
                JsonNode jsonNode = globalConfig.getJsonValue();
                exploreFilters.setOfferCategories(mapper.convertValue(jsonNode.get("categories"), new TypeReference<List<String>>(){}));
            }
            exploreFilters.setOfferSubTypes(Arrays.asList(INSTORE_SHOPPING, ONLINE_SHOPPING));
            exploreFilters.setExploreTypes(Arrays.asList(PROMOTIONAL_OFFER, OFFER, NOTIFICATION));
            return exploreFilters;
        } catch (Exception exception) {
            log.error("Exception getting offer categories: {}", exception.getMessage());
            throw new Exception("Error getting offer categories, Please try again later");
        }
    }


    @Override
    public void pushNotifications(ExploreDAO exploreDAO) throws Exception{
        String exploreType = exploreDAO.getExploreType();
        switch (exploreType.toUpperCase()) {
            case "NOTIFICATION":
                generateNotificationDao("info", exploreDAO, "homeScreen");
                break;
            case "OFFER":
                generateNotificationDao("offer", exploreDAO, "explore");
                break;
            case "PROMOTIONAL-OFFER":
                generateNotificationDao("promotional-offer", exploreDAO, "homeScreen");
                break;
            default:
                log.info("Offer Not allowed to send push notifications");
        }
    }

    private void generateNotificationDao(String type, ExploreDAO exploreDAO, String action) throws Exception {
        List<AuthenticationDAO> authenticationDAOS = exploreDAO.getAuthenticationDAOSet().stream().collect(Collectors.toList());
        if(authenticationDAOS.isEmpty())
            return;
        AddNotificationDTO notificationDTO = getNotificationObject(exploreDAO.getExploreId(), exploreDAO.getCurrentStatus(), type);
        String body = exploreDAO.getDescription();
        body = body.length()>200?body.substring(0,200)+"...":body;
        notificationDTO.setAction(action);
        notificationDTO.setTitle(exploreDAO.getTitle());
        notificationDTO.setBody(body);
        notificationDTO.setSubTitle(body);

        if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
            SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
            for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                sortedSlotDAO.add(slotDAO);
            }
            LocalDate endDate = sortedSlotDAO.last().getEndDate();
            String endTime = sortedSlotDAO.last().getEndTime();
            Date expiryDate = CommonUtils.getDateObject(endDate, endTime);
            notificationDTO.setExpiryDate(expiryDate);
        }

        notificationService.saveNotification(authenticationDAOS, notificationDTO);
    }

    private AddNotificationDTO getNotificationObject(String typeId, String status, String type){
        AddNotificationDTO addNotificationDTO = new AddNotificationDTO();
        addNotificationDTO.setType(type);
        addNotificationDTO.setTypeId(typeId);
        addNotificationDTO.setStatus(status);
        return addNotificationDTO;
    }

    @Override
    public Double getNearByExploerRadiusGC() {
        // Default Nearby radius
        double dist = 2000;
        try{
            GlobalConfigDTO configDTO = globalConfigService.getGlobalConfig(NEARBY_EXPLORE_RADIUS, false);
            if (configDTO!=null){
                log.info("Fetch nearby explore radius from db");
                dist = Integer.parseInt(configDTO.getValue());
            }
        } catch (Exception ex){
            log.error("failed to get nearBy explore distance using default, Exception: {}",ex.getMessage());
        }
        return dist;
    }

    @Override
    public List<String> readTargettingFile(MultipartFile file) throws Exception {
        log.info("request received in readTargettingFile method: ");
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        List<String> result = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                int rowNum = nextRow.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                Cell cell = nextRow.getCell(0);
                String value = null;
                switch(cell.getCellType().ordinal()) {
                    case 2:
                        value = cell.getStringCellValue();
                        break;
                    case 1:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            value = dateFormat.format(cell.getDateCellValue());
                        } else {
                            value = (long)cell.getNumericCellValue()+"";
                        }
                        break;
                    default:
                        throw new Exception("Invalid content Type, Mobile Number should be in Numeric, DoB should be in Date format and AppId should be in string format");
                }

                result.add(value);
            }
        } catch (Exception exception) {
            log.error("Failed to read data: {}",exception.getMessage());
        }
        return result;
    }

    @Override
    @Transactional
    public void mapExploreItems(AuthenticationDAO authenticationDAO) throws Exception {
        log.info("Inside mapExploreItems method:");
        if (!authenticationDAO.getUserType().equals(TRANSIT_USER)) {
            log.debug("User is not a transit App user: {}", CommonUtils.maskString(authenticationDAO.getUserName(),0,authenticationDAO.getUserName().length()-4,'*'));
            return;
        }
        DAOUser daoUser = authenticationDAO.getDaoUser();
        try {
            List<ExploreDAO> exploreDAOList = exploreRepository.findAllByCurrentStatusAndIsActiveAndTargetAudienceIsNotNull(PUBLISHED, true);
            for (ExploreDAO exploreDAO: exploreDAOList) {
                if (exploreDAO.getSlotDAOSet() !=null && !exploreDAO.getSlotDAOSet().isEmpty()) {
                    SortedSet<SlotDAO> sortedSlotDAO = new TreeSet<>(Comparator.comparing(SlotDAO::getStartDate));
                    for (SlotDAO slotDAO: exploreDAO.getSlotDAOSet()) {
                        sortedSlotDAO.add(slotDAO);
                    }
                    LocalDate endDate = sortedSlotDAO.last().getEndDate();
                    String endTime = sortedSlotDAO.last().getEndTime();
                    try {
                        if (CommonUtils.checkIfOfferExpired(endDate, endTime)) {
                            continue;
                        }
                    } catch (Exception exception) {
                        log.error("Error in checking expiration");
                        continue;
                    }
                }
                TargetAudienceDTO targetAudience = null;
                try {
                    targetAudience = exploreDAO.getTargetAudience();
                } catch (Exception exception) {
                    log.error("Exception in getting target audience: {}", exception.getMessage());
                    continue;
                }

                for (TargetOptionDTO targetOptionDTO : targetAudience.getData()) {
                    switch (targetOptionDTO.getFilter()) {
                        case CUSTOMISED_TARGETTING:
                            ExploreFilterDTO exploreFilterDTO = targetOptionDTO.getSubFilters().get(0);
                            Set<AuthenticationDAO> authenticationDAOSet = new HashSet<>();
                            switch (exploreFilterDTO.getSelectedVal()) {
                                case DOB:
                                    List<LocalDate> dobList = exploreFilterDTO.getListVals().stream().map(v -> CommonUtils.getLocalDate(v, "dd/MM/yyyy")).collect(Collectors.toList());
                                    List<DAOUser> daoUsers = userRepository.findByDobIn(dobList);
                                    if (dobList.contains(daoUser.getDob())) {
                                        authenticationDAO.getExploreDAOSet().add(exploreDAO);
                                        exploreDAO.getAuthenticationDAOSet().add(authenticationDAO);
                                    }
                                    break;
                            }
                            break;
                        case ALL_KOCHI1_CARD_USERS:
                            if (authenticationDAO.getCardDetailsDAO() != null) {
                                String cardNum = authenticationDAO.getCardDetailsDAO().getCardNo();
                                if (!CommonUtils.isNullOrEmpty(cardNum)) {
                                    authenticationDAO = addSubFilters(targetOptionDTO, exploreDAO, authenticationDAO);
                                }
                            }
                            break;
                        case APP_ONLY_USERS:
                            if (authenticationDAO.getCardDetailsDAO() != null) {
                                String cardNum = authenticationDAO.getCardDetailsDAO().getCardNo();
                                if (CommonUtils.isNullOrEmpty(cardNum)) {
                                    authenticationDAO = addSubFilters(targetOptionDTO, exploreDAO, authenticationDAO);
                                }
                            } else {
                                authenticationDAO = addSubFilters(targetOptionDTO, exploreDAO, authenticationDAO);
                            }
                            break;
                        case ALL_USERS:
                            authenticationDAO = addSubFilters(targetOptionDTO, exploreDAO, authenticationDAO);
                            break;
                    }
                }
            }

            exploreRepository.saveAll(exploreDAOList);
            authenticationRepository.save(authenticationDAO);
        } catch (Exception exception) {
            log.error("Exception in mapExploreItems: {}", exception.getMessage());
        }
    }

    private AuthenticationDAO addSubFilters(TargetOptionDTO targetOptionDTO, ExploreDAO exploreDAO, AuthenticationDAO authenticationDAO) {
        List<Gender> genderList = new ArrayList<>();
        List<String> occupationList = new ArrayList<>();
        boolean checkAge = false;
        boolean checkOccupation = false;
        boolean checkGender = false;
        Double minAge = 0.0, maxAge = 0.0;
        for (ExploreFilterDTO filterDTO : targetOptionDTO.getSubFilters()) {
            switch (filterDTO.getName()) {
                case AGE:
                    minAge = filterDTO.getMinVal();
                    maxAge = filterDTO.getMaxVal();
                    LocalDate currentDate = LocalDate.now();
                    LocalDate minDob = currentDate.minusYears(maxAge.longValue());
                    LocalDate maxDob = currentDate.minusYears(minAge.longValue());
                    if (authenticationDAO.getDaoUser().getDob().isAfter(minDob) && authenticationDAO.getDaoUser().getDob().isBefore(maxDob)) {
                        checkAge = true;
                    }
                    break;
                case OCCUPATION:
                    occupationList = filterDTO.getListVals();
                    if (occupationList.contains(authenticationDAO.getDaoUser().getOccupation())) {
                        checkOccupation = true;
                    }
                    break;
                case GENDER:
                    genderList = filterDTO.getListVals().stream().map(Gender::valueOf).collect(Collectors.toList());
                    // Map for users without gender specified
                    genderList.add(NA);
                    if (genderList.contains(authenticationDAO.getDaoUser().getGender())) {
                        checkGender = true;
                    }
                    break;
            }
        }

        if (checkAge && checkOccupation && checkGender) {
            authenticationDAO.getExploreDAOSet().add(exploreDAO);
            exploreDAO.getAuthenticationDAOSet().add(authenticationDAO);
        }
        return authenticationDAO;
    }
}