package com.axisbank.transit.authentication.config;

import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DTO.UserDetailsAdminDTO;
import com.axisbank.transit.authentication.repository.RoleRepository;
import com.axisbank.transit.authentication.service.AuthAdminService;
import com.axisbank.transit.bus.service.BusTimeTableService;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.model.response.QuickBookDefaultDTO;
import com.axisbank.transit.core.model.response.QuickBookDefaultDetailsDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.journey.services.JourneyService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.axisbank.transit.core.shared.constants.ClickActions.topUp;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.*;
import static com.axisbank.transit.core.shared.constants.RoleConstants.*;
import static com.axisbank.transit.explore.constants.CustomizedTargettingName.*;
import static com.axisbank.transit.payment.constants.ServiceProviderConstant.KMRL;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.ACTIVE;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.TEMP_BLOCK;

@Slf4j
@Component
public class ApplicationSetupData implements ApplicationListener<ContextRefreshedEvent> {
    List<String> occupations = Arrays.asList("Bussiness Professional", "Medical / Healthcare Professional", "Government / Civil Services", "Student", "Retired", "Educator", "Homemaker", "Hospitality", "Transportation", "Sales", "Technology / Engineer", "Other");
    List<String> offerCategories = Arrays.asList("Dining",
            "Education",
            "Entertainment",
            "Food Delivery",
            "Grocery",
            "Hotel",
            "Jewellery",
            "Online Doctor Consultation",
            "Others",
            "Pharmacy",
            "Retail ",
            "Services",
            "Shopping",
            "Spa & Wellness",
            "Travel",
            "Wellness"
            );
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private Environment environment;
    public static String cardSecretKey;

    @Autowired
    RedisClient redisClient;

    @Autowired
    AuthAdminService authAdminService;

    @Autowired
    GlobalConfigService globalConfigService;
    @Autowired
    BusTimeTableService busTimeTableService;
    @Autowired
    JourneyService journeyService;

    @Value("${superuser.admin.first-name}")
    private String firstName;
    @Value("${superuser.admin.last-name}")
    private String lastName;
    @Value("${superuser.admin.mobile}")
    private String mobile;
    @Value("${superuser.admin.email}")
    private String email;
    @Value("${superuser.admin.dob}")
    private String dob;
    @Value("${superuser.admin.username}")
    private String username;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createRoleIfNotFound(ADMIN_ROLE);
        createRoleIfNotFound(USER_ROLE);
        createRoleIfNotFound(CHECKER);
        createRoleIfNotFound(MAKER);
        createRoleIfNotFound(PUBLISHER);
        cardSecretKey = environment.getProperty("card.secret.key");
        String redisPattern = "bus_route:*";
        try {
            redisClient.deletePattern(redisPattern);
            log.info("Redis patter:{} removed",redisPattern);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",redisPattern, ex.getMessage());
        }
        // TODO remove this once ldap integrated
        createDefaultUserIfNotFound();
        cleanGlobalConfigCache();
        try{
            initiateGlobalConfigs();
        } catch (Exception ex){
            log.info("Failed to initiate global configs: {}", ex.getMessage());
        }
        try{
            cleanSuggestedRouteCache();
            cleanBusRouteCache();
            cleanMetroRouteCache();
            journeyService.setSuggestedGraph();
        } catch (Exception exception){
            log.error("Failed to generate suggested graph: {}", exception.getMessage());
        }
    }

    public static String getCardSecretKey() {
        return ApplicationSetupData.cardSecretKey;
    }

    @Transactional
    void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByNameAndIsActive(roleName, true);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }

    void createDefaultUserIfNotFound(){
        if(email.isEmpty())
            return;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(dob,df);
        List<String> roles= Arrays.asList(ADMIN_ROLE, CHECKER, MAKER,PUBLISHER);
        UserDetailsAdminDTO userDetailsAdminDTO = new UserDetailsAdminDTO(firstName, "",lastName,null,
                username,"portal-user",null, email, ld, roles);
        try {
            authAdminService.createUser(userDetailsAdminDTO);
            log.debug("Default user creation successful");
        } catch (Exception ex){
            log.debug("Default User already exist");
        }
    }

    void cleanGlobalConfigCache(){
        String redisPattern = "global_config:*";
        try {
            redisClient.deletePattern(redisPattern);
            log.info("Redis pattern:{} removed",redisPattern);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",redisPattern, ex.getMessage());
        }
    }

    void cleanSuggestedRouteCache(){
        String redisPattern = "suggested_route:*";
        try {
            redisClient.deletePattern(redisPattern);
            log.info("Redis pattern:{} removed",redisPattern);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",redisPattern, ex.getMessage());
        }
    }
    void cleanBusRouteCache(){
        String redisPattern = "bus_route:*";
        try {
            redisClient.deletePattern(redisPattern);
            log.info("Redis pattern:{} removed",redisPattern);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",redisPattern, ex.getMessage());
        }
    }

    void cleanMetroRouteCache(){
        String redisPattern = "metro_route:*";
        try {
            redisClient.deletePattern(redisPattern);
            log.info("Redis pattern:{} removed",redisPattern);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",redisPattern, ex.getMessage());
        }
    }

    void initiateGlobalConfigs(){
        addDefaultQuickBookConfig();
        addDefaultCardBins();
        addValidCardRegistrationTypes();
        addKochiCardDetails();
        addPaymentModeOffers();
        addAppTncUrl();
        addExpirenceAxisUrl();
        addEFormPageUrl();
        addMinBalanceNotification();
        addTargetAudience();
        addNearByStationRadius();
        addMaxWalkDistance();
        addNearByExploreRadius();
        addOfferCategories();
        addAsisCancellationWindow();
        addCardLimitDetails();
        addTicketRefreshInterval();
        addKmrlFareChartURL();
        addAppMinVersion();
    }

    private void addKmrlFareChartURL() {
        String key = KMRL_FARE_CHART_URL;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"https://s3-ap-south-1.amazonaws.com/kmrldata/wp-content/uploads/2020/10/22113639/KMRL_Farechart1.jpg",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add Ticket Refresh Interval");
            }
        }
    }

    private void addCardLimitDetails() {
        String key = CARD_LIMIT_DETAILS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("totalCardBalanceLimitWithMinKYC", "5000.00");
            map.put("totalCardBalanceLimitWithFullKYC", "20000.00");
            map.put("chipBalanceMinLimit", "500");
            map.put("chipBalanceMaxLimit", "2000");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid age range");
            }
        }
    }

    private void addTargetAudience() {
        addValidAgeRange();
        addValidGenders();
        addValidOccupations();
        addValidCustomizedTargettingNames();
    }

    private void addValidCustomizedTargettingNames() {
        String key = VALID_CUSTOMIZED_TARGETTING_NAMES;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            List<String> targettingVals = Arrays.asList(TRANSIT_APP_ID, MOBILE_NUMBER, DOB);
            Map<String, List<String>> map = new HashMap<>();
            map.put("targettingVals", targettingVals);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid occupations");
            }
        }
    }

    private void addValidOccupations() {
        String key = VALID_OCCUPATIONS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, List<String>> map = new HashMap<>();
            map.put("occupations", occupations);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid occupations");
            }
        }
    }

    private void addOfferCategories() {
        String key = OFFER_CATEGORIES;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, List<String>> map = new HashMap<>();
            map.put("categories", offerCategories);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set offer categories");
            }
        }
    }

    private void addValidGenders() {
        String key = VALID_GENDERS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            List<String> genders = Stream.of(Gender.values())
                    .map(Gender::name)
                    .collect(Collectors.toList());
            Map<String, List<String>> map = new HashMap<>();
            map.put("genders", genders);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid genders");
            }
        }
    }

    private void addValidAgeRange() {
        String key = VALID_AGE_RANGE;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("minVal", "10");
            map.put("maxVal", "70");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid age range");
            }
        }
    }

    void addDefaultQuickBookConfig(){
        String rdKey = "quick_book_default";
        try {
            redisClient.deleteKey(rdKey);
            log.info("Redis key:{} removed",rdKey);
        } catch (Exception ex){
            log.debug("Failed to Delete redis key:{}, Exception:{}",rdKey, ex.getMessage());
        }
        String key = QUICK_BOOK_DEFAULT;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            QuickBookDefaultDTO quickBookDefaultDTO = new QuickBookDefaultDTO();
            List<QuickBookDefaultDetailsDTO> quickBookDefaultDetailsDTOList = new ArrayList<>();
            quickBookDefaultDetailsDTOList.add(new QuickBookDefaultDetailsDTO("ALVA","MACE", KMRL));
            quickBookDefaultDetailsDTOList.add(new QuickBookDefaultDetailsDTO("MACE","ALVA", KMRL));
            quickBookDefaultDetailsDTOList.add(new QuickBookDefaultDetailsDTO("EDAP","ALVA", KMRL));
            quickBookDefaultDTO.setDefaultOptions(quickBookDefaultDetailsDTOList);
            JsonNode node = mapper.valueToTree(quickBookDefaultDTO);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set quickBook data");
            }
        }
    }

    void addDefaultCardBins(){
        String key = TRANSIT_CARD_BINS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            List<String> bins= Arrays.asList("508997", "508980");
            Map<String, List<String>> map = new HashMap<>();
            map.put("cardBins", bins);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set transitcard bins");
            }
        }
    }
    void addValidCardRegistrationTypes(){
        String key = TRANSIT_CARD_VALID_REGISTRATION_STATUS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            List<String> bins= Arrays.asList(TEMP_BLOCK, ACTIVE);
            Map<String, List<String>> map = new HashMap<>();
            map.put("validTypes", bins);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }

    void addKochiCardDetails(){
        String key = KOCHI_CARD_FEE_DETAILS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("issuanceFee", "10");
            map.put("annualFee", "10");
            map.put("topupFee", "11");
            map.put("productType", "Physical");
            map.put("tnc", "https://example.com/tnc.html");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }
    void addPaymentModeOffers(){
        String key = PAYMENT_OFFERS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, Map<String, String>> map = new HashMap<>();
            Map<String, String> kochiPaymentMap = new HashMap<>();
            kochiPaymentMap.put("offerText", "Use Transit-card and get 10% discount");
            kochiPaymentMap.put("tncUrl", "https://example.com/tnc.html");
            map.put("kochi1Card", kochiPaymentMap);
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }
    void addAppTncUrl(){
        String key = APP_TnC_URL;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"https://example.com/tnc.html",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }
    void addExpirenceAxisUrl(){
        String key = EXPERIENCE_AXIS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"https://example.com/experience.html",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }

    void addEFormPageUrl(){
        String key = EFORM_PAGE;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"https://example.com/e-forms.html",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }

    void addMinBalanceNotification(){
        String key = MIN_BALANCE_NOTIFICATION;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("title", "Your Kochi1 Card balance is low");
            map.put("subtitle", "Your KMRL Axis Bank Kochi1 Card balance is low. Recharge your card for quick travel.");
            map.put("action", topUp.toString());
            map.put("minBalance", "100");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to set valid registration cards");
            }
        }
    }
    void addMaxWalkDistance(){
        String key = MAX_WALK_DISTANCE;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"2000",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add Max Walk Distance");
            }
        }
    }

    void addNearByStationRadius(){
        String key = NEARBY_STATION_RADIUS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"2000",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add NearBy Radius");
            }
        }
    }

    void addNearByExploreRadius(){
        String key = NEARBY_EXPLORE_RADIUS;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"2000",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add NearBy Radius");
            }
        }
    }

    void addTicketRefreshInterval(){
        String key = TICKET_REFRESH_INTERVAL;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, false);
        if (globalConfig==null){
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,"10",null, false);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add Ticket Refresh Interval");
            }
        }
    }

    void addAsisCancellationWindow(){
        String key = AFC_CANCELLATION_WINDOW;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("from", "06:00:00");
            map.put("to", "22:00:00");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed AFC cancellation window");
            }
        }
    }

    void addAppMinVersion(){
        String key = APP_MIN_VERSION;
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(key, true);
        if (globalConfig==null){
            Map<String, String> map = new HashMap<>();
            map.put("android", "1.4");
            map.put("ios", "1.0");
            JsonNode node = mapper.valueToTree(map);
            GlobalConfigDTO gfd = new GlobalConfigDTO(key,null,node,true);
            try {
                globalConfigService.setGlobalConfig(gfd);
            } catch (Exception ex){
                log.info("Failed to add APP min version");
            }
        }
    }
}
