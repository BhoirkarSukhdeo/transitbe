package com.axisbank.transit.userDetails.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;
import com.axisbank.transit.userDetails.model.DTO.UserConfigDTO;
import com.axisbank.transit.userDetails.model.DTO.UserDetailsDTO;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.service.UserInfoService;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.TRANSIT_USER;

@Slf4j
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserRepository userDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    GlobalConfigService globalConfigService;

    @Autowired
    ExploreService exploreService;

    @Autowired
    ExploreRepository exploreRepository;

    /**
     * get user details og particular UserId
     * @param userId
     * @return
     */
    public UserDetailsDTO getUserDetails(String userId) throws Exception{
        log.info("Request received in getUserDetails method with userId: "+userId);
        UserDetailsDTO result = null;
        Boolean isCardLinked = false;
        try {
            DAOUser daoUser = userRepository.findByUserIdAndIsActive(userId, true);
            AuthenticationDAO authenticationDAO = daoUser.getAuthenticationDAO();

            if (authenticationDAO.getCardDetailsDAO() != null) {
                isCardLinked = true;
            }
            String fullName = CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName());
            SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
            result =  new UserDetailsDTO(
                    daoUser.getUserId(),
                  fullName,
                  authenticationDAO.getMobile(),
                  authenticationDAO.getUserName(),
                  daoUser.getGender(),
                  authenticationDAO.getEmail(),
                  daoUser.getDob(),
                  daoUser.getOccupation(),
                    isCardLinked,
                    sessionDAO!=null?sessionDAO.getLastApiAccessTime():CommonUtils.currentDateTime(),
                    authenticationDAO.getUserType(),
                    authenticationDAO.getActive(),
                    authenticationDAO.getRolesList()
            );
        } catch (Exception ex) {
            log.error("Error in getting User Details {}", ex.getMessage());
            throw new Exception("Error in fetching user details, Please try again later.");
        }
        return result;
    }

    /**
     * test method to save user details
     * @param userDetails
     * @return
     */
    public AuthenticationDAO updateUserDetails(UserDetailsDTO userDetails) throws Exception{
        log.info("Request received in updateUserDetails method: ");
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            DAOUser daoUser = authenticationDAO.getDaoUser();
            daoUser.setGender(userDetails.getGender());
            daoUser.setOccupation(userDetails.getOccupation());
            authenticationDAO.setEmail(userDetails.getEmail());
            String mobile = userDetails.getMobile();
            if (!CommonUtils.isNullOrEmpty(mobile)) {
                if (authenticationDAO.getCardDetailsDAO() == null) {
                    authenticationDAO.setMobile(userDetails.getMobile());
                    authenticationDAO.setUserName(userDetails.getMobile());
                }
            }

            if (authenticationDAO.getUserType().equals(TRANSIT_USER)) {
                GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true);
                if (globalConfig != null) {
                    JsonNode genderNode = globalConfig.getJsonValue();
                    List<String> occupations= mapper.convertValue(genderNode.get("occupations"), new TypeReference<List<String>>(){});
                    if (!occupations.contains(daoUser.getOccupation())) {
                        daoUser.setOccupation("Other");
                    }
                }
            }

            authenticationDAO.setDaoUser(daoUser);
            daoUser.setAuthenticationDAO(authenticationDAO);

            try {
                if (authenticationDAO !=null ) {
                    for (ExploreDAO u : authenticationDAO.getExploreDAOSet()) {
                        u.getAuthenticationDAOSet().remove(authenticationDAO);
                    }
                    exploreRepository.saveAll(authenticationDAO.getExploreDAOSet());
                    exploreService.mapExploreItems(authenticationDAO);
                }

            } catch (Exception exception) {
                log.error("Exception in mapping user with explore items: {}", exception.getMessage());
            }
            authenticationDAO = authenticationRepository.save(authenticationDAO);
            return authenticationDAO;
        } catch (Exception ex) {
            log.error("Error in updating user details: {}",ex.getMessage());
            throw ex;
        }
    }

    /**
     * Method to fetch LoggedIn user details
     * @return
     * @throws Exception
     */
    public UserDetailsDTO getLoggedInUserDetails() throws Exception{
        log.info("Request received in getLoggedInUserDetails method");
        UserDetailsDTO result = null;
        Boolean isCardLinked = false;
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            DAOUser daoUser = authenticationDAO.getDaoUser();

            if (authenticationDAO.getUserType().equals(TRANSIT_USER)) {
                GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true);
                if (globalConfig != null) {
                    JsonNode genderNode = globalConfig.getJsonValue();
                    List<String> occupations= mapper.convertValue(genderNode.get("occupations"), new TypeReference<List<String>>(){});
                    if (!(occupations.contains(daoUser.getOccupation()))) {
                        daoUser.setOccupation("Other");
                        authenticationDAO.setDaoUser(daoUser);
                        authenticationRepository.save(authenticationDAO);
                    }
                }
            }

            if (authenticationDAO.getCardDetailsDAO() != null) {
                isCardLinked = true;
            }
            String fullName = CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName());
            SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
            result =  new UserDetailsDTO(
                    daoUser.getUserId(),
                    fullName,
                    authenticationDAO.getMobile(),
                    authenticationDAO.getUserName(),
                    daoUser.getGender(),
                    authenticationDAO.getEmail(),
                    daoUser.getDob(),
                    daoUser.getOccupation(),
                    isCardLinked,
                    sessionDAO!=null?sessionDAO.getLastApiAccessTime():CommonUtils.currentDateTime(),
                    authenticationDAO.getUserType(),
                    authenticationDAO.getActive(),
                    authenticationDAO.getRolesList()
            );
        } catch (Exception ex) {
            log.error("Error in getting User Details: {}", ex.getMessage());
            throw ex;
        }
        return result;
    }

    public UserConfigDTO getUserConfig() throws Exception {
        log.info("Request received in getUserConfig method");
        UserConfigDTO userConfigDTO = new UserConfigDTO();
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            userConfigDTO.setPushNotificationsStatus(!CommonUtils.isNullOrEmpty(authenticationDAO.getDeviceInfo().getFcmToken()));
        } catch (Exception exception) {
            userConfigDTO.setPushNotificationsStatus(false);
        }
        return userConfigDTO;
    }

    /**
     * Method to update sharedPreference user details
     * @return
     * @throws Exception
     */
    public DAOUser updateUserConfiguration (UserConfigurationDTO userConfiguration) throws Exception{
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            DAOUser daoUser = authenticationDAO.getDaoUser();
            daoUser.setUserConfiguration(userConfiguration);
            authenticationDAO.setDaoUser(daoUser);
            daoUser.setAuthenticationDAO(authenticationDAO);
            return userRepository.save(daoUser);
        } catch (Exception ex) {
            log.error("Error in updating shared prefrence details: {}",ex.getMessage());
            throw ex;
        }
    }
}
