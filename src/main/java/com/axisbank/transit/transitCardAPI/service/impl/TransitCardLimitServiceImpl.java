package com.axisbank.transit.transitCardAPI.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.transitCardAPI.TransitCardClient.CardLimitsClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DAO.CardLimitDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.*;
import com.axisbank.transit.transitCardAPI.model.request.GetCustomer;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.UpdateCardLimitRequestRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.RequestBody;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.UpdateCardOfflineAmountRequest;
import com.axisbank.transit.transitCardAPI.repository.CardLimitDetailsRepository;
import com.axisbank.transit.transitCardAPI.service.TransitCardLimitService;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.CARD_LIMIT_DETAILS;
import static com.axisbank.transit.transitCardAPI.constants.TransactionLimitType.LIMIT_TYPE_MAP;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.LANGUAGE;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.MBRID;

@Slf4j
@Service
public class TransitCardLimitServiceImpl implements TransitCardLimitService {

    @Value("${update.cardlimit.userId}")
    private String userId;

    @Value("${update.cardlimit.password}")
    private String password;

    @Autowired
    private TransitCardClient transitCardClient;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private TransitCardTxnService transitCardTxnService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private CardLimitsClient cardLimitsClient;

    @Autowired
    CardLimitDetailsRepository cardLimitDetailsRepository;

    @Override
    public CardLimitsDTO getLimits() throws Exception {
        log.info("Request received in getLimitsMethod");
        CardLimitsDTO cardLimitsDTO = new CardLimitsDTO();
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
            JsonNode customerDetailJson = getCustomerDetailJson(cardDetailsDAO);
            String KYCStatus = customerDetailJson.get("KYCStatus").asText();
            ArrayNode cardLimitsSummaryArrayNode = getCardLimitSummary(cardDetailsDAO);
            for (JsonNode jsonNode: cardLimitsSummaryArrayNode) {
                String limitType = jsonNode.get("LimtTypesObj").get("LimitType").asText();
                if (limitType.equalsIgnoreCase("T")) {
                    LimitTypeDetailDTO limitTypeDetailDTO =  getLimitTypeDetailObject(jsonNode, limitType, KYCStatus);
                    cardLimitsDTO.setRetailPOSContactlessLimit(limitTypeDetailDTO);
                }
                if (limitType.equalsIgnoreCase("I")) {
                    LimitTypeDetailDTO limitTypeDetailDTO =  getLimitTypeDetailObject(jsonNode, limitType, KYCStatus);
                    cardLimitsDTO.setRetailPOSContactLimit(limitTypeDetailDTO);
                }
                if (limitType.equalsIgnoreCase("E")) {
                    LimitTypeDetailDTO limitTypeDetailDTO =  getLimitTypeDetailObject(jsonNode, limitType, KYCStatus);
                    cardLimitsDTO.setOnlineSpendsLimit(limitTypeDetailDTO);
                }
            }
            GlobalConfigDTO ageGlobalConfig = globalConfigService.getGlobalConfig(CARD_LIMIT_DETAILS, true);
            if (ageGlobalConfig != null) {
                JsonNode globalConfigJsonValue = ageGlobalConfig.getJsonValue();
                cardLimitsDTO.setFullKYCLimit(globalConfigJsonValue.get("totalCardBalanceLimitWithFullKYC").asText());
                if (KYCStatus.equalsIgnoreCase("Y2")) {
                    cardLimitsDTO.setFullKYC(true);
                    cardLimitsDTO.setTotalCardBalanceLimit(globalConfigJsonValue.get("totalCardBalanceLimitWithFullKYC").asText());
                } else {
                    cardLimitsDTO.setFullKYC(false);
                    cardLimitsDTO.setTotalCardBalanceLimit(globalConfigJsonValue.get("totalCardBalanceLimitWithMinKYC").asText());
                }
                LimitTypeDetailDTO chipBalanceLimit = getChipBalanceObject(globalConfigJsonValue, authenticationDAO);
                cardLimitsDTO.setChipBalanceLimit(chipBalanceLimit);
                double eBalanceLimit = Double.parseDouble(cardLimitsDTO.getTotalCardBalanceLimit()) - Double.parseDouble(cardLimitsDTO.getChipBalanceLimit().getCurrent());
                cardLimitsDTO.seteBalanceLimit(String.format("%.2f", eBalanceLimit));
            }
        } catch (Exception exception) {
            log.error("Exception in getLimits: {}", exception.getMessage());
            throw new Exception("Error in getting current Limit data, Please try later");
        }
        return cardLimitsDTO;
    }

    private LimitTypeDetailDTO getLimitTypeDetailObject(JsonNode jsonNode, String limitType, String KYCStatus){
        CardTxnLimitTypeAllDetails cardTxnLimitTypeAllDetails = setLimitDetails(jsonNode);
        LimitTypeDetailDTO limitTypeDetailDTO = new LimitTypeDetailDTO();
        limitTypeDetailDTO.setLimitType(limitType);
        limitTypeDetailDTO.setDisplayName(LIMIT_TYPE_MAP.get(limitType).getDisplayName());
        limitTypeDetailDTO.setMin("1");
        String currentLimit = cardTxnLimitTypeAllDetails.getCardMaxSingleAmount();
        if (currentLimit.equalsIgnoreCase("0")) {
            limitTypeDetailDTO.setCurrent(cardTxnLimitTypeAllDetails.getProductMaxSingleAmount());
        } else {
            limitTypeDetailDTO.setCurrent(cardTxnLimitTypeAllDetails.getCardMaxSingleAmount());
        }
        if (KYCStatus.equalsIgnoreCase("Y2")) {
            limitTypeDetailDTO.setMax(cardTxnLimitTypeAllDetails.getCardMaxDailyAmount());
        } else {
            limitTypeDetailDTO.setMax(cardTxnLimitTypeAllDetails.getProductMaxSingleAmount());
        }
        limitTypeDetailDTO.setEnabled(cardTxnLimitTypeAllDetails.isEnabled());
        return limitTypeDetailDTO;
    }

    private LimitTypeDetailDTO getChipBalanceObject(JsonNode globalConfigJsonValue, AuthenticationDAO authenticationDAO) throws Exception{
        LimitTypeDetailDTO chipBalanceLimit = new LimitTypeDetailDTO();
        chipBalanceLimit.setDisplayName("CARD CHIP BALANCE LIMIT");
        chipBalanceLimit.setMin(globalConfigJsonValue.get("chipBalanceMinLimit").asText());
        chipBalanceLimit.setMax(globalConfigJsonValue.get("chipBalanceMaxLimit").asText());
        chipBalanceLimit.setCurrent(String.format("%.2f", 2000.0)); //TODO fetch from db
        chipBalanceLimit.setUpdatedOn(authenticationDAO.getCardDetailsDAO().getCreatedAt()); //TODO fetch from db
        CardLimitDetailsDAO cardLimitDetailsDAO = authenticationDAO.getCardLimitDetailsDAO();
        if (cardLimitDetailsDAO != null) {
            if (cardLimitDetailsDAO.getChipBalanceLimit() != null) {
                LimitTypeDetailDTO limitTypeDetailDTO = cardLimitDetailsDAO.getChipBalanceLimit();
                chipBalanceLimit.setCurrent(String.format("%.2f",Double.parseDouble(limitTypeDetailDTO.getCurrent())));
                chipBalanceLimit.setUpdatedOn(limitTypeDetailDTO.getUpdatedOn());
            }
        }
        return chipBalanceLimit;
    }

    public JsonNode getCustomerDetailJson(CardDetailsDAO cardDetailsDAO) throws Exception{
        TransitCardInfoDTO cardInfoDTO = transitCardTxnService.getTransitCardInfo(cardDetailsDAO.getCardNo());
        String customerNumber = cardInfoDTO.getCustomerNo();
        GetCustomer customer = new GetCustomer();
        customer.setCustomerNo(customerNumber);
        JsonNode customerJson = transitCardClient.getCustomer(customer);
        JsonNode custRes = customerJson.get("GetCustomerResponse").get("GetCustomerResult");
        if (custRes.get("Result").asText().equalsIgnoreCase("Error")){
            throw new Exception(custRes.get("ReturnDescription").asText());
        }
        return custRes.get("Customer");
    }

    public ArrayNode getCardLimitSummary(CardDetailsDAO cardDetailsDAO) throws Exception{
        GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalanceInfoRequest =
                transitCardTxnService.getCardAllLimitAndBalanceInfoRequest(cardDetailsDAO);
        JsonNode getAllLimitJsonNode;
        try {
            getAllLimitJsonNode = transitCardClient.getCardAllLimitAndBalanceInfo(getCardAllLimitAndBalanceInfoRequest);
        } catch (Exception exception) {
            log.error("Exception in getCardAllLimitAndBalanceInfo api: {}",exception.getMessage());
            throw new Exception("Error in fetching details, Please try again later");
        }

        JsonNode getAllLimitsResult = getAllLimitJsonNode.get("GetAllLimitAndBalanceInfoResponse").get("GetAllLimitAndBalanceInfoResult");
        if(!getAllLimitsResult.get("Result").asText().equalsIgnoreCase("Success")) throw new Exception("Error in fetching details, Please try again later");

        return  (ArrayNode) getAllLimitsResult.get("AllLimitsDictionary").get("CardLimitsSummary");
    }

    private CardTxnLimitTypeAllDetails setLimitDetails(JsonNode jsonNode) {
        CardTxnLimitTypeAllDetails cardTxnLimitTypeAllDetails = new CardTxnLimitTypeAllDetails();
        ArrayNode limitDetailList = (ArrayNode) jsonNode.get("LimitDetailList").get("LimitDetail");
        for (JsonNode limitDetail: limitDetailList) {
            String limitProfileDesc = limitDetail.get("LimitProfileDesc").asText().toUpperCase().trim();
            switch (limitProfileDesc) {
                case "CARD":
                    if (limitDetail.get("MaxSingleAmount").asText().equals("0")) {
                        cardTxnLimitTypeAllDetails.setEnabled(false);
                    }
                    cardTxnLimitTypeAllDetails.setCardCheckDailyAmount(limitDetail.get("CheckDailyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckWeeklyAmount(limitDetail.get("CheckWeeklyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckMonthlyAmount(limitDetail.get("CheckMonthlyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckYearlyAmount(limitDetail.get("CheckYearlyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckSingleAmount(limitDetail.get("CheckSingleAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckDailyCount(limitDetail.get("CheckDailyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckMonthlyCount(limitDetail.get("CheckMonthlyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckYearlyCount(limitDetail.get("CheckYearlyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardCheckWeeklyCount(limitDetail.get("CheckWeeklyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxDailyAmount(limitDetail.get("MaxDailyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxWeeklyAmount(limitDetail.get("MaxWeeklyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxMonthlyAmount(limitDetail.get("MaxMonthlyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxYearlyAmount(limitDetail.get("MaxYearlyAmount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxDailyCount(limitDetail.get("MaxDailyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxMonthlyCount(limitDetail.get("MaxMonthlyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxYearlyCount(limitDetail.get("MaxYearlyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxWeeklyCount(limitDetail.get("MaxWeeklyCount").asText());
                    cardTxnLimitTypeAllDetails.setCardMaxSingleAmount(limitDetail.get("MaxSingleAmount").asText());
                    break;
                case "PRODUCT":
                    cardTxnLimitTypeAllDetails.setProductMaxSingleAmount(limitDetail.get("MaxSingleAmount").asText());
                    break;
                default:
                    log.debug("No LimitProfileDesc matching");
                    break;
            }
        }
        return cardTxnLimitTypeAllDetails;
    }

    @Override
    @Transactional
    public UpdateChipBalanceResponseDTO updateCardChipLimit(double amount) throws Exception {
        log.info("Request Received in updateCardChipLimit with amount: {}", amount);
        try {
            String currentTotalLimit = "";
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
            UpdateCardOfflineAmountRequest updateCardOfflineAmountRequest = new UpdateCardOfflineAmountRequest();
            RequestBody requestBody = new RequestBody();
            requestBody.setUserId(userId);
            requestBody.setPassword(password);
            requestBody.setMbrId(MBRID);
            requestBody.setLanguage(LANGUAGE);

            GlobalConfigDTO ageGlobalConfig = globalConfigService.getGlobalConfig(CARD_LIMIT_DETAILS, true);
            if (ageGlobalConfig != null) {
                JsonNode globalConfigJsonValue = ageGlobalConfig.getJsonValue();
                JsonNode customerDetailJson = getCustomerDetailJson(cardDetailsDAO);
                String KYCStatus = customerDetailJson.get("KYCStatus").asText();

                if (KYCStatus.equalsIgnoreCase("Y2")) {
                    currentTotalLimit = globalConfigJsonValue.get("totalCardBalanceLimitWithFullKYC").asText();
                } else {
                    currentTotalLimit = globalConfigJsonValue.get("totalCardBalanceLimitWithMinKYC").asText();
                }
                double chipMaxLimit = Double.parseDouble(globalConfigJsonValue.get("chipBalanceMaxLimit").asText());
                if (amount > chipMaxLimit) {
                    throw new Exception("Current Limit can't be more than Max Limit.");
                }
            }
            requestBody.setAmount(String.format("%.2f", amount));
            requestBody.setCardNumber(cardDetailsDAO.getCardNo());
            updateCardOfflineAmountRequest.setRequestBody(requestBody);
            JsonNode jsonNode = cardLimitsClient.updateCardOfflineAmount(updateCardOfflineAmountRequest);
            JsonNode responseBody = jsonNode.get("updateCardOfflineAmountResponse").get("responseBody");
            if(!responseBody.get("status").asText().equalsIgnoreCase("Success")) throw new Exception("Error in Updating Limit, Please try again later");

            LimitTypeDetailDTO limitTypeDetailDTO = new LimitTypeDetailDTO();
            limitTypeDetailDTO.setCurrent(String.format("%.2f", amount));
            limitTypeDetailDTO.setUpdatedOn(new Date());
            CardLimitDetailsDAO cardLimitDetailsDAO = authenticationDAO.getCardLimitDetailsDAO();
            if (cardLimitDetailsDAO == null) {
                cardLimitDetailsDAO = new CardLimitDetailsDAO();
                cardLimitDetailsDAO.setLimitDetailId(CommonUtils.generateRandomString(30));
                authenticationDAO.setCardLimitDetailsDAO(cardLimitDetailsDAO);
                cardLimitDetailsDAO.setAuthenticationDAO(authenticationDAO);
            }
            cardLimitDetailsDAO.setChipBalanceLimit(limitTypeDetailDTO);
            cardLimitDetailsRepository.save(cardLimitDetailsDAO);
            UpdateChipBalanceResponseDTO updateChipBalanceResponseDTO = new UpdateChipBalanceResponseDTO();
            updateChipBalanceResponseDTO.setChipBalanceLimit(limitTypeDetailDTO);
            double eBalanceLimit = Double.parseDouble(currentTotalLimit) - Double.parseDouble(limitTypeDetailDTO.getCurrent());
            updateChipBalanceResponseDTO.seteBalanceLimit(String.format("%.2f", eBalanceLimit));
            return updateChipBalanceResponseDTO;
        } catch (Exception exception) {
            log.error("Error in Updating Limit, Please try after sometime");
            throw new Exception(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public LimitTypeDetailDTO updateCardTxnLimit(UpdateCardTxnLimitDTO updateCardTxnLimitDTO) throws Exception {
        log.info("Request Received in updateCardTxnLimit method");
        LimitTypeDetailDTO limitTypeDetailDTOUpdated = null;
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
            JsonNode customerDetailJson = getCustomerDetailJson(cardDetailsDAO);
            String KYCStatus = customerDetailJson.get("KYCStatus").asText();
            ArrayNode cardLimitsSummaryArrayNode = getCardLimitSummary(cardDetailsDAO);
            for (JsonNode jsonNode: cardLimitsSummaryArrayNode) {
                String limitType = jsonNode.get("LimtTypesObj").get("LimitType").asText();
                if (limitType.equalsIgnoreCase(updateCardTxnLimitDTO.getLimitType())) {
                    UpdateCardLimitRequestRequest updateCardLimitRequestRequest = new UpdateCardLimitRequestRequest();
                    com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody requestBody = getUpdateLimitRequestBody(jsonNode, limitType, cardDetailsDAO.getCardNo(), updateCardTxnLimitDTO, KYCStatus);
                    if (!updateCardTxnLimitDTO.isEnabled()) {
                        requestBody.setMaximumSingleAmount("0");
                    }
                    CardLimitDetailsDAO cardLimitDetailsDAO = authenticationDAO.getCardLimitDetailsDAO();
                    if (cardLimitDetailsDAO == null) {
                        cardLimitDetailsDAO = new CardLimitDetailsDAO();
                        cardLimitDetailsDAO.setLimitDetailId(CommonUtils.generateRandomString(30));
                        authenticationDAO.setCardLimitDetailsDAO(cardLimitDetailsDAO);
                        cardLimitDetailsDAO.setAuthenticationDAO(authenticationDAO);
                    }
                    LimitTypeDetailDTO limitTypeDetailDTO = new LimitTypeDetailDTO();
                    limitTypeDetailDTO.setCurrent(String.valueOf(updateCardTxnLimitDTO.getAmount()));
                    limitTypeDetailDTO.setUpdatedOn(new Date());
                    limitTypeDetailDTO.setEnabled(updateCardTxnLimitDTO.isEnabled());

                    switch (limitType) {
                        case "T":
                            cardLimitDetailsDAO.setRetailPOSContactless(limitTypeDetailDTO);
                            requestBody.setRestrictOnlineContactlessTransaction(String.valueOf(!updateCardTxnLimitDTO.isEnabled()));
                            break;
                        case "I":
                            cardLimitDetailsDAO.setRetailPOSContact(limitTypeDetailDTO);
                            requestBody.setRestrictEmvTransactionWithoutPin(String.valueOf(!updateCardTxnLimitDTO.isEnabled()));
                            break;
                        case "E":
                            cardLimitDetailsDAO.setOnlineSpends(limitTypeDetailDTO);
                            requestBody.setRestrictEcommerceTransaction(String.valueOf(!updateCardTxnLimitDTO.isEnabled()));
                            break;
                        default:
                            log.debug("Invalid LimitType provided");
                            break;
                    }
                    updateCardLimitRequestRequest.setRequestBody(requestBody);
                    JsonNode response = cardLimitsClient.updateCardLimit(updateCardLimitRequestRequest);
                    JsonNode responseBody = response.get("updateCardLimitResponse").get("responseBody");
                    if(!responseBody.get("status").asText().equalsIgnoreCase("Success")) throw new Exception("Error in Updating Limit, Please try again later");
                    cardLimitDetailsRepository.save(cardLimitDetailsDAO);
                    continue;
                }
            }

            ArrayNode cardLimitsSummaryArrayNodeUpdated = getCardLimitSummary(cardDetailsDAO);
            for (JsonNode jsonNode: cardLimitsSummaryArrayNodeUpdated) {
                String limitType = jsonNode.get("LimtTypesObj").get("LimitType").asText();
                if (limitType.equalsIgnoreCase(updateCardTxnLimitDTO.getLimitType())) {
                    limitTypeDetailDTOUpdated =  getLimitTypeDetailObject(jsonNode, limitType, KYCStatus);
                }
            }
        } catch (Exception exception) {
            log.error("Exception in updateCardTxnLimit: {}", exception.getMessage());
            throw new Exception(exception.getMessage());
        }
        return limitTypeDetailDTOUpdated;
    }

    private com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody getUpdateLimitRequestBody(JsonNode jsonNode, String limitType, String cardNumber, UpdateCardTxnLimitDTO updateCardTxnLimitDTO, String KYCStatus) throws Exception{
        CardTxnLimitTypeAllDetails cardTxnLimitTypeAllDetails = setLimitDetails(jsonNode);
        com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody requestBody = new com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody();
        requestBody.setUserId(userId);
        requestBody.setPassword(password);
        requestBody.setMbrId(MBRID);
        requestBody.setLanguage(LANGUAGE);
        requestBody.setLimitType(limitType);
        requestBody.setCardNumber(cardNumber);
        requestBody.setDailyAmount(cardTxnLimitTypeAllDetails.getCardCheckDailyAmount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setWeeklyAmount(cardTxnLimitTypeAllDetails.getCardCheckWeeklyAmount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setMonthlyAmount(cardTxnLimitTypeAllDetails.getCardCheckMonthlyAmount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setYearlyAmount(cardTxnLimitTypeAllDetails.getCardCheckYearlyAmount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setDailyCount(cardTxnLimitTypeAllDetails.getCardCheckDailyCount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setWeeklyCount(cardTxnLimitTypeAllDetails.getCardCheckWeeklyCount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setMonthlyCount(cardTxnLimitTypeAllDetails.getCardCheckMonthlyCount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setYearlyCount(cardTxnLimitTypeAllDetails.getCardCheckYearlyCount().equalsIgnoreCase("true") ? "1" : "0");
        requestBody.setSingleAmount(cardTxnLimitTypeAllDetails.getCardCheckSingleAmount().equalsIgnoreCase("true") ? "1" : "0");
        String currentAmount = cardTxnLimitTypeAllDetails.getCardMaxSingleAmount();
        double maxAmount = 0.0;
        if (KYCStatus.equalsIgnoreCase("Y2")) {
            maxAmount = Double.parseDouble(cardTxnLimitTypeAllDetails.getCardMaxDailyAmount());
        } else {
            maxAmount = Double.parseDouble(cardTxnLimitTypeAllDetails.getProductMaxSingleAmount());
        }
        if (updateCardTxnLimitDTO.getAmount() > maxAmount) {
            throw new Exception("Current Limit can't be more than Max Limit.");
        } else if (currentAmount.equals("0")) {
            requestBody.setMaximumSingleAmount(String.valueOf(maxAmount));
        } else {
            requestBody.setMaximumSingleAmount(String.valueOf(updateCardTxnLimitDTO.getAmount()));
        }
        requestBody.setMaximumDailyAmount(cardTxnLimitTypeAllDetails.getCardMaxDailyAmount());
        requestBody.setMaximumWeeklyAmount(cardTxnLimitTypeAllDetails.getCardMaxWeeklyAmount());
        requestBody.setMaximumMonthlyAmount(cardTxnLimitTypeAllDetails.getCardMaxMonthlyAmount());
        requestBody.setMaximumYearlyAmount(cardTxnLimitTypeAllDetails.getCardMaxYearlyAmount());
        requestBody.setMaximumDailyCount(cardTxnLimitTypeAllDetails.getCardMaxDailyCount());
        requestBody.setMaximumWeeklyCount(cardTxnLimitTypeAllDetails.getCardMaxWeeklyCount());
        requestBody.setMaximumMonthlyCount(cardTxnLimitTypeAllDetails.getCardMaxMonthlyCount());
        requestBody.setMaximumYearlyCount(cardTxnLimitTypeAllDetails.getCardMaxYearlyCount());
        return requestBody;
    }
}
