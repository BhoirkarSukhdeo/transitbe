package com.axisbank.transit.authentication.service.impl;

import com.axisbank.transit.authentication.constants.RegistrationType;
import com.axisbank.transit.authentication.exceptions.InvalidMpinException;
import com.axisbank.transit.authentication.exceptions.InvalidRefreshTokenException;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.exceptions.MpinValidationException;
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
import com.axisbank.transit.authentication.service.AuthService;
import com.axisbank.transit.authentication.service.CustomUserDetailsService;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.constants.RoleConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.core.shared.utils.RedisClient;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.FinacleClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.request.GetCustomer;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationResp;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCustomerDtlsRequest.GetCustomerDtlsRequest;
import com.axisbank.transit.transitCardAPI.model.request.getEntityDoc.GetEntityDocRequest;
import com.axisbank.transit.transitCardAPI.model.request.getEntityDoc.RequestBody;
import com.axisbank.transit.transitCardAPI.model.response.GetEntityDocRecord;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.VALID_OCCUPATIONS;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.EXPIRED;
import static com.axisbank.transit.userDetails.constants.Gender.NA;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransitCardClient transitCardClient;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private FinacleClient finacleClient;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    MpinRepository mpinRepository;

    @Autowired
    TransitCardTxnService transitCardTxnService;
    @Autowired
    GlobalConfigService globalConfigService;

    @Autowired
    ExploreRepository exploreRepository;

    @Autowired
    ExploreService exploreService;

    @Value("${transit.card.allow.auto-link}")
    boolean allowAutoLink;

    @Value("${app.mpin.length.validation}")
    int mpinLengthValidation;

    @Autowired
    RedisClient redisClient;

    /**
     * save user data into the database while registering
     * @param user
     * @return
     */
    @Transactional
    public AuthenticationDAO saveUser(AuthDTO user) throws Exception {
        log.info("Request Received in saveUser: ");
        ObjectMapper mapper = new ObjectMapper();
        AuthenticationDAO authenticationDAO = null;
        DAOUser daoUser = null;
        String mobile_number =null;

        try {
            if (user.getRegistrationType() == RegistrationType.hasCardWithNumber) {
                CardDetailsDAO cardDetailsDAO = null;
                mobile_number = user.getMobile();
                deleteUserIfUnverified(mobile_number);
                authenticationDAO = new AuthenticationDAO();
                daoUser = new DAOUser();
                cardDetailsDAO = new CardDetailsDAO();
                daoUser.setUserId(CommonUtils.generateRandomString(30));

                CardVerificationResp cardVerificationResp = new CardVerificationResp(user.getLastFourDigitCardNumber(),"", mobile_number);
                CardVerificationRespRequest cardVerificationRespRequest = new CardVerificationRespRequest();
                cardVerificationRespRequest.setCardVerificationResp(cardVerificationResp);
                JsonNode cardVerificationJson = transitCardClient.cardVerificationResp(cardVerificationRespRequest);
                JsonNode result = cardVerificationJson.get("CardVerificationResponse").get("CardVerificationResult");
                if (result.get("Result").asText().equalsIgnoreCase("Error")){
                    throw new Exception(result.get("ReturnDescription").asText());
                }
                String cardNumber = result.get("CardList").get("string").asText();
                if(!transitCardTxnService.verifyCardBin(cardNumber))
                    throw new Exception("Card not allowed");
                cardDetailsDAO.setCardNo(cardNumber);
                cardDetailsDAO.setCardToken(CommonUtils.generateRandomString(30));

                TransitCardInfoDTO cardInfoDTO = transitCardTxnService.getTransitCardInfo(cardNumber);
                String customerNumber = cardInfoDTO.getCustomerNo();
                String status = cardInfoDTO.getCardStatCode();
                String subStatus = cardInfoDTO.getCardStatSubCode();
                String completeStatus = transitCardTxnService.getBlockStatus(status,subStatus);
                log.debug("Card Status before registration: {}", completeStatus);
                if(!transitCardTxnService.isRegistrationAllowed(completeStatus)) {
                    if(completeStatus.equalsIgnoreCase(EXPIRED))
                        throw new Exception("Your card has been expired");
                    throw new Exception("Your card has been permanently block");
                }
                GetCustomer customer = new GetCustomer();
                customer.setCustomerNo(customerNumber);
                JsonNode customerJson = transitCardClient.getCustomer(customer);
                JsonNode custRes = customerJson.get("GetCustomerResponse").get("GetCustomerResult");
                if (custRes.get("Result").asText().equalsIgnoreCase("Error")){
                    throw new Exception(custRes.get("ReturnDescription").asText());
                }
                JsonNode customerDetailJson = custRes.get("Customer");
                daoUser.setFirstName(customerDetailJson.get("Name").asText());
                daoUser.setMiddleName(customerDetailJson.get("MidName").asText());
                daoUser.setLastName(customerDetailJson.get("Surname").asText());
                daoUser.setOccupation(customerDetailJson.get("Occupation").asText());
                if (customerDetailJson.get("Gender").asText().equalsIgnoreCase("M")) {
                    daoUser.setGender(Gender.MALE);
                } else if (customerDetailJson.get("Gender").asText().equalsIgnoreCase("F")) {
                    daoUser.setGender(Gender.FEMALE);
                } else {
                    daoUser.setGender(Gender.TRANSGENDER);
                }
                String dateString = customerDetailJson.get("BirthDate").asText();
                int year = Integer.parseInt(dateString.substring(0,4));
                int month = Integer.parseInt(dateString.substring(4,6));
                int day = Integer.parseInt(dateString.substring(6,8));
                LocalDate localDate = LocalDate.of(year,month,day);
                daoUser.setDob(localDate);
                authenticationDAO.setMobile(mobile_number);
                authenticationDAO.setUserName(mobile_number);
                authenticationDAO.setEmail(customerDetailJson.get("Email").asText());
                authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
                cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
            }

            if (user.getRegistrationType() == RegistrationType.hasCardWithoutNumber) {
                log.info("Request receive for registering user with hasCardWithoutNumber type Registration");
                mobile_number= user.getMobile();
                authenticationDAO = getTransitCardAndValidation(user, RegistrationType.hasCardWithoutNumber);
                daoUser = authenticationDAO.getDaoUser();
            }

            if (user.getRegistrationType() == RegistrationType.NoCardIsCustomer) {
                log.info("Request receive for registering customer with NoCardIsCustomer type Registration");
                String custId = user.getCifId();
                GetCustomerDtlsRequest customerDtlsRequest = new GetCustomerDtlsRequest();
                customerDtlsRequest.setCustId(custId);
//                Transit getCustomerDetails API Finacle module for fetching name, mobile and email based on cifId.
                JsonNode getCustDetailsJson =finacleClient.getCustomerDetails(customerDtlsRequest);
                if (getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE)==null){
                    throw new Exception("Failed to get Customer Details");
                }
                String matchFound = getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE).get("matchFound").asText();
                if(!matchFound.equalsIgnoreCase("True")) throw new Exception("Failed to match Customer Details");
                JsonNode getCustomerDtlsRes = getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE).get("CustomerDetails");
                String name = getCustomerDtlsRes.get("name").asText().trim();
                mobile_number= CommonUtils.getMobileWithCountryCode(getCustomerDtlsRes.get("mobile").asText());
                String emailId =getCustomerDtlsRes.get("email").asText();
//                Validation for pan number using Finacle Client using getEntityDoc API call.
                GetEntityDocRequest getEntityDocRequest = new GetEntityDocRequest();
                RequestBody requestBody = new RequestBody();
                requestBody.setDocCode(TransitCardAPIConstants.PAN);
                requestBody.setReferenceNumber(user.getPanNumber());
                getEntityDocRequest.setRequestBody(requestBody);
                JsonNode getEntityDocJson = finacleClient.getEntityDoc(getEntityDocRequest);
                if (getEntityDocJson.get(TransitCardAPIConstants.GET_ENTITY_DOC_RESPONSE)==null){
                    throw new Exception("Failed to get EntityDoc Details");
                }
                JsonNode responseBodyNode = getEntityDocJson.get(TransitCardAPIConstants.GET_ENTITY_DOC_RESPONSE).get("ResponseBody");
                String matchFound1= responseBodyNode.get("matchFound").asText();
                if(!matchFound1.equalsIgnoreCase("TRUE")) {
                    throw new Exception("Invalid Pan Number");
                }
                List<GetEntityDocRecord> docRecords= Arrays.asList(mapper.readValue(responseBodyNode.
                        get(TransitCardAPIConstants.GET_ENTITY_DOC_RECORD).toString(), GetEntityDocRecord[].class));
                if(docRecords.size() == 0) {
                    throw new Exception("Failed to get Pan Number");
                }
                boolean isCifValid=false;
                for(GetEntityDocRecord docRecord: docRecords){
                    if(docRecord.getOrgkey().equalsIgnoreCase(user.getCifId()))
                    {
                        isCifValid=true;
                        break;
                    }
                }
                if(!isCifValid) {
                    throw new Exception("Failed to match cifID");
                }
                deleteUserIfUnverified(mobile_number);
                authenticationDAO = new AuthenticationDAO();
                daoUser = new DAOUser();
                daoUser.setUserId(CommonUtils.generateRandomString(30));
                processFullName(name, daoUser);
                authenticationDAO.setMobile(mobile_number);
                authenticationDAO.setUserName(mobile_number);
                authenticationDAO.setEmail(emailId);
                daoUser.setDob(user.getDob());
            }

            if (user.getRegistrationType() == RegistrationType.NoCardNotCustomer) {
                authenticationDAO = new AuthenticationDAO();
                daoUser = new DAOUser();
                mobile_number = user.getMobile();
                deleteUserIfUnverified(mobile_number);
                try {
                  authenticationDAO =getTransitCardAndValidation(user, RegistrationType.NoCardNotCustomer);
                } catch (Exception ex) {
                    log.error("No Kochi1 card found with this user: "+user.getName());
                }
                daoUser.setUserId(CommonUtils.generateRandomString(30));
                daoUser.setFirstName(user.getName());
                daoUser.setLastName(user.getLastName());
                authenticationDAO.setMobile(mobile_number);
                authenticationDAO.setUserName(mobile_number);
                authenticationDAO.setEmail(user.getEmailId());
                daoUser.setDob(user.getDob());
            }
            GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(VALID_OCCUPATIONS, true);
            if (globalConfig != null) {
                JsonNode genderNode = globalConfig.getJsonValue();
                List<String> occupations= mapper.convertValue(genderNode.get("occupations"), new TypeReference<List<String>>(){});
                if (!occupations.contains(daoUser.getOccupation())) {
                    daoUser.setOccupation("Other");
                }
            }
            authenticationDAO.setDaoUser(daoUser);
            Customer customer = paymentService.createCustomer(mobile_number, daoUser.getUserId());
            Gender userGender= daoUser.getGender()!=null?daoUser.getGender():NA;
            daoUser.setGender(userGender);
            daoUser.setAuthenticationDAO(authenticationDAO);
            daoUser.setPgCustomerId(customer.getId());
            authenticationDAO = authenticationRepository.save(authenticationDAO);
            try {
                if (authenticationDAO != null) {
                    exploreService.mapExploreItems(authenticationDAO);
                }
            } catch (Exception exception) {
                log.error("Exception in mapping user with explore items: {}", exception.getMessage());
            }
            return authenticationDAO;
        } catch (Exception ex) {
            log.error("Exception in save user: {}", ex.getMessage());
                throw ex;
        }
    }

    public AuthenticationDAO getTransitCardAndValidation(AuthDTO user, RegistrationType registrationType) throws Exception {
        log.info("Request receive for fetching all TransitCards and validate");
        if(!allowAutoLink && registrationType==RegistrationType.NoCardNotCustomer)
            throw new Exception("Auto Link Disabled");
        CardDetailsDAO cardDetailsDAO = new CardDetailsDAO();
        String mobile_number = user.getMobile();
        deleteUserIfUnverified(mobile_number);
        AuthenticationDAO authenticationDAO = new AuthenticationDAO();
        DAOUser daoUser = new DAOUser();
        daoUser.setUserId(CommonUtils.generateRandomString(30));
        JSONObject cardDetails = transitCardTxnService.fetchCustomerCardInfo(
                user.getMobile(), user.getDob().toString(),user.getName(),user.getLastName());
        String completeStatus = cardDetails.getString("CompleteStatus");
        String cardNumber = cardDetails.getString("CardNo");
        String customerNumber = cardDetails.getString("CustomerNo");
        log.debug("Card Status before registration: {}", completeStatus);
        if(!transitCardTxnService.isRegistrationAllowed(completeStatus)) {
            if(completeStatus.equalsIgnoreCase(EXPIRED))
                throw new Exception("Your card has been expired");
            throw new Exception("Your card has been permanently block");
        }
        if(!transitCardTxnService.verifyCardBin(cardNumber))
            throw new Exception("Card not allowed");
        cardDetailsDAO.setCardNo(cardNumber);
        cardDetailsDAO.setCardToken(CommonUtils.generateRandomString(30));
        JSONObject customerDetails = userDetailsFromGetCustomer(customerNumber);
        daoUser.setFirstName(user.getName());
        daoUser.setLastName(user.getLastName());
        authenticationDAO.setMobile(mobile_number);
        authenticationDAO.setUserName(mobile_number);
        authenticationDAO.setEmail(customerDetails.getString("Email"));
        daoUser.setDob(user.getDob());
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
        cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
        authenticationDAO.setDaoUser(daoUser);
        return authenticationDAO;
    }

    private JSONObject userDetailsFromGetCustomer(String customerNumber) throws Exception {
        JSONObject customerDetails = new JSONObject();
        GetCustomer customer = new GetCustomer();
        customer.setCustomerNo(customerNumber);
        JsonNode customerJson = transitCardClient.getCustomer(customer);
        JsonNode custRes = customerJson.get("GetCustomerResponse").get("GetCustomerResult");
        if (custRes.get("Result").asText().equalsIgnoreCase("Error")){
            throw new Exception(custRes.get("ReturnDescription").asText());
        }
        JsonNode customerDetailJson = custRes.get("Customer");
        customerDetails.put("Occupation",customerDetailJson.get("Occupation").asText());
        customerDetails.put("Email", customerDetailJson.get("Email").asText());
        if (customerDetailJson.get("Gender").asText().equalsIgnoreCase("M")) {
            customerDetails.put("Gender", Gender.MALE.toString());
        } else if (customerDetailJson.get("Gender").asText().equalsIgnoreCase("F")) {
            customerDetails.put("Gender", Gender.FEMALE.toString());
        } else {
            customerDetails.put("Gender", Gender.TRANSGENDER.toString());
        }

        return customerDetails;
    }


    /**
     * extract first, middle, last name from full name and save it in user table
     * @param fullName
     * @param newUser
     */
    public void processFullName(String fullName, DAOUser newUser) {
        String[] fullNameArr = fullName.split(" ");
        if (fullNameArr.length > 0) {
            switch (fullNameArr.length) {
                case 1:
                    newUser.setFirstName(fullNameArr[0]);
                    break;
                case 2:
                    newUser.setFirstName(fullNameArr[0]);
                    newUser.setLastName(fullNameArr[1]);
                    break;
                case 3:
                    newUser.setFirstName(fullNameArr[0]);
                    newUser.setMiddleName(fullNameArr[1]);
                    newUser.setLastName(fullNameArr[2]);
                    break;
            }
        }
    }

    /**
     * check if the mobile number is already present in db
     * @param mobile_number
     * @throws Exception
     */
    public void deleteUserIfUnverified(String mobile_number) throws Exception {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByMobileAndIsActive(mobile_number, true);
        if (authenticationDAO !=null){
            if(authenticationDAO.getOtpVerification()) {
                throw new Exception("User already registered.");
            } else {
                deleteUser(authenticationDAO);
            }
        }
    }

    /**
     * set otpVerification flag to true for a particular mobile number
     * @param mobile
     */
    public void enableOtpVerication(String mobile, LocalDateTime currentDateTime) {
        try {
            AuthenticationDAO authenticationDAO = authenticationRepository.findByMobileAndIsActive(mobile, true);
            authenticationDAO.setLastlogin(currentDateTime);
            authenticationDAO.setOtpVerification(true);
            authenticationRepository.save(authenticationDAO);

        } catch (Exception ex) {
            log.error("Error in enableOtpVerification method: {}",ex.getMessage());
            throw ex;
        }
    }

    public void setUserRole(AuthenticationDAO savedUser) {
        Role userRole = roleRepository.findByNameAndIsActive(RoleConstants.USER_ROLE, true);
        if (userRole != null) {
            Collection<Role> authRoles = new ArrayList<>();
            authRoles.add(userRole);
            savedUser.setRoles(authRoles);
            authenticationRepository.save(savedUser);
        }
    }

    @Override
    @Transactional
    public void deleteUser(String mobileNumber) throws Exception {
        AuthenticationDAO user = authenticationRepository.findByMobileAndIsActive(mobileNumber, true);
        if (user == null) {
            throw new Exception("User not found");
        }
        user.getRoles().forEach(u -> u.getAuthenticationDAOS().remove(user));
        user.getExploreDAOSet().forEach(u -> u.getAuthenticationDAOSet().remove(user));
        exploreRepository.saveAll(user.getExploreDAOSet());
        roleRepository.saveAll(user.getRoles());
        authenticationRepository.delete(user);
        authenticationRepository.flush();
    }

    @Async
    public void saveRefreshTokenAndLastAccessTime(String refreshToken, String username) {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByUserNameIgnoreCaseAndIsActive(username, true);
        SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
        if (sessionDAO == null ) {
            sessionDAO = new SessionDAO();
            sessionDAO.setSessionRefId(CommonUtils.generateRandomString(30));
        }

        if (refreshToken != null) {
            sessionDAO.setRefreshToken(refreshToken);
        }
        sessionDAO.setLastApiAccessTime(CommonUtils.currentDateTime());
        authenticationDAO.setSessionDAO(sessionDAO);
        sessionDAO.setAuthenticationDAO(authenticationDAO);
        sessionRepository.save(sessionDAO);
    }

    public void checkRefreshToken(String refreshToken, String username) throws Exception{
        AuthenticationDAO authenticationDAO = authenticationRepository.findByUserNameIgnoreCaseAndIsActive(username, true);
        if (authenticationDAO==null)
            throw new UsernameNotFoundException("User Not found for given refresh Token");
        SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
        if (!sessionDAO.getRefreshToken().equals(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid Refresh Token");
        }
    }

    @Override
    public void deleteUser(AuthenticationDAO user) throws Exception {
        user.getRoles().forEach(u -> u.getAuthenticationDAOS().remove(user));
        roleRepository.saveAll(user.getRoles());
        user.getExploreDAOSet().forEach(u -> u.getAuthenticationDAOSet().remove(user));
        exploreRepository.saveAll(user.getExploreDAOSet());
        authenticationRepository.delete(user);
        authenticationRepository.flush();
    }

    @Override
    public AuthenticationDAO setMpin(SetMpinDTO setMpinDTO) throws Exception {
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            String username = authenticationDAO.getUserName();
            String otkKey = getOtkRedis(username);
            String currentMpin = getCleanMpin(setMpinDTO.getCurrentMpin(), otkKey);
            String mpin = getCleanMpin(setMpinDTO.getMpin(), otkKey);
            String confirmMpin = getCleanMpin(setMpinDTO.getConfirmMpin(), otkKey);
            setMpinDTO.setCurrentMpin(currentMpin);
            setMpinDTO.setMpin(mpin);
            setMpinDTO.setConfirmMpin(confirmMpin);
            clearOtkKey(username);
            if (setMpinDTO.getCurrentMpin() != null) {
                checkMpinWithAuth(authenticationDAO, setMpinDTO.getCurrentMpin());
            }
            if (!(setMpinDTO.getConfirmMpin().equals(setMpinDTO.getMpin()))) {
                throw new MpinValidationException("MPIN did not match");
            }

            if (CommonUtils.allCharactersSame(setMpinDTO.getMpin())) {
                throw new MpinValidationException("All Digits should not be same");
            }
            if (CommonUtils.checkConsecutive(setMpinDTO.getMpin())) {
                throw new MpinValidationException("MPIN should not contain consecutive digits");
            }

            Pageable requestedPage = PageRequest.of(0, 3, Sort.by("id").descending());
            List<MpinLog> recentMpinLogs = mpinRepository.findAllByAuthenticationDAO_Id(authenticationDAO.getId(), requestedPage);
            if (recentMpinLogs != null && recentMpinLogs.size() > 0) {
                for (MpinLog mpinLog : recentMpinLogs) {
                    if (BCrypt.checkpw(setMpinDTO.getMpin(), mpinLog.getMpin())) {
                        throw new MpinValidationException("New MPIN should not be same as last 3 MPINs");
                    }
                }
            }

            String encryptedMpin = BCrypt.hashpw(setMpinDTO.getMpin(), BCrypt.gensalt());
            authenticationDAO.setMpin(encryptedMpin);
            Set<MpinLog> mpinLogSet = authenticationDAO.getMpins();
            MpinLog mpinLog = new MpinLog();
            mpinLog.setMpin(encryptedMpin);
            mpinLog.setAuthenticationDAO(authenticationDAO);
            mpinLogSet.add(mpinLog);
            authenticationDAO.setMpins(mpinLogSet);
            // Set User attempt to 0, on setting new MPIN
            SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
            sessionDAO.setUserAttempts(0);
            sessionRepository.save(sessionDAO);
            // --
            return   authenticationRepository.save(authenticationDAO);
        } catch (Exception exception) {
            log.error("Error in set MPIN: {}",exception.getMessage());
            throw exception;
        }
    }

    public void checkMpin(String username, String mpin) throws Exception {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByMobileAndIsActive(username, true);
        String otkKey = getOtkRedis(username);
        mpin = getCleanMpin(mpin,otkKey);
        clearOtkKey(username);
        checkMpinWithAuth(authenticationDAO, mpin);
    }

    public void checkMpinWithAuth(AuthenticationDAO authenticationDAO, String mpin) throws Exception{
        SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
        checkBlockedUser(sessionDAO);
        int userAttempts = sessionDAO.getUserAttempts();
        if (!(BCrypt.checkpw(mpin, authenticationDAO.getMpin())) && userAttempts < 5) {
            log.info("User attempts : {}", userAttempts+1);
            sessionDAO.setUserAttempts(userAttempts+1);
            sessionRepository.save(sessionDAO);
            throw new InvalidMpinException("Invalid MPIN");
        } else if(userAttempts >= 5) {
            // If user mpin attempt is greater than 5, block the user account
            log.info("blocked user account after entering 5 incorrect mpin: {}");
            sessionDAO.setBlocked(true);
            sessionDAO.setUnBlockTime(null);
            sessionDAO.setBlockTime(CommonUtils.currentDateTime());
            sessionRepository.save(sessionDAO);
            throw new MpinBlockedUserException("Your account is blocked, Please retry after 5 minutes.");
        } else {
            // If user mpin attempt is successful, reset attempt to 0
            sessionDAO.setUserAttempts(0);
            sessionRepository.save(sessionDAO);
        }
    }

    public void checkBlockedUser(SessionDAO sessionDAO) throws MpinBlockedUserException{
        if (sessionDAO.isBlocked()) {
            Duration duration = Duration.between(sessionDAO.getBlockTime(), CommonUtils.currentDateTime());
            log.debug("Blocked duration in mins: {}",duration.toMinutes());
            if (duration.toMinutes() > 5) {
                sessionDAO.setBlocked(false);
                sessionDAO.setBlockTime(null);
                sessionDAO.setUnBlockTime(CommonUtils.currentDateTime());
                sessionDAO.setUserAttempts(0);
            } else {
                throw new MpinBlockedUserException("Your account is blocked, Please retry after sometime");
            }
        }
    }

    @Override
    public Boolean confirmCifId(AuthDTO authDTO) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String mobile_number;
        String custId = authDTO.getCifId();
        GetCustomerDtlsRequest customerDtlsRequest = new GetCustomerDtlsRequest();
        customerDtlsRequest.setCustId(custId);
//        Transit getCustomerDetails API Finacle module for fetching name, mobile and email based on cifId.
        JsonNode getCustDetailsJson =finacleClient.getCustomerDetails(customerDtlsRequest);
        if (getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE)==null){
            throw new Exception("Failed to get Customer Details");
        }
        String matchFound = getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE).get("matchFound").asText();
        if(!matchFound.equalsIgnoreCase("True")) throw new Exception("Failed to match Customer Details");
        JsonNode getCustomerDtlsRes = getCustDetailsJson.get(TransitCardAPIConstants.GET_CUSTOMER_DTLS_RESPONSE).get("CustomerDetails");
        String name = getCustomerDtlsRes.get("name").asText();
        mobile_number= getCustomerDtlsRes.get("mobile").asText();
        String emailId =getCustomerDtlsRes.get("email").asText();
//        Validation for pan number using Finacle Client using getEntityDoc API call.
        GetEntityDocRequest getEntityDocRequest = new GetEntityDocRequest();
        RequestBody requestBody = new RequestBody();
        requestBody.setDocCode(TransitCardAPIConstants.PAN);
        requestBody.setReferenceNumber(authDTO.getPanNumber());
        getEntityDocRequest.setRequestBody(requestBody);
        JsonNode getEntityDocJson = finacleClient.getEntityDoc(getEntityDocRequest);
        if (getEntityDocJson.get(TransitCardAPIConstants.GET_ENTITY_DOC_RESPONSE)==null){
            throw new Exception("Failed to get EntityDoc Details");
        }
            JsonNode responseBodyNode = getEntityDocJson.get(TransitCardAPIConstants.GET_ENTITY_DOC_RESPONSE).get("ResponseBody");
            String matchFound1= responseBodyNode.get("matchFound").asText();
            if(!matchFound1.equalsIgnoreCase("TRUE")) {
                throw new Exception("Invalid Pan Number");
            }
            List<GetEntityDocRecord> docRecords= Arrays.asList(mapper.readValue(responseBodyNode.
                    get(TransitCardAPIConstants.GET_ENTITY_DOC_RECORD).toString(), GetEntityDocRecord[].class));
            if(docRecords.size() == 0) {
                throw new Exception("Failed to get Pan Number");
            }
            boolean isCifValid=false;
            for(GetEntityDocRecord docRecord: docRecords){
                if(docRecord.getOrgkey().equalsIgnoreCase(authDTO.getCifId()))
                {
                    isCifValid=true;
                    break;
                }
            }
            return isCifValid;
    }

    @Override
    public String generateOTK(String refreshToken, String username) throws Exception {
        checkRefreshToken(refreshToken,username);
        return generateOTKRedis(username);
    }

    private String generateOTKRedis(String username){
        String redisKey = "userOTK:"+username;
        String otk = CommonUtils.generateRandomString(16);
        redisClient.setValue(redisKey, otk, 60);
        return otk;
    }
    private String getOtkRedis(String username){
        String redisKey = "userOTK:"+username;
        return redisClient.getValue(redisKey);
    }

    private void clearOtkKey(String username){
        String redisKey = "userOTK:"+username;
        redisClient.deleteKey(redisKey);
    }

    private String getCleanMpin(String encryptedData, String key) throws Exception {
        // backward compatible to check if mpin is plain text number.
        if(encryptedData == null)
            return encryptedData;
        if (encryptedData.length() == mpinLengthValidation && CommonUtils.isNumeric(encryptedData))
            return encryptedData;
        if(key == null)
            throw new Exception("Invalid data provided");
        return EncryptionUtil.decryptText(encryptedData, key);
    }

    @Override
    public AuthenticationDAO getMobileNumber(String userName){
    AuthenticationDAO authenticationDAO=   authenticationRepository.findByUserName(userName);
       return authenticationDAO;
    }
}
