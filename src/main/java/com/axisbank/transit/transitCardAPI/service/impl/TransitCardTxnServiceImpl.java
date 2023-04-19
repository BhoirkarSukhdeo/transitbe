package com.axisbank.transit.transitCardAPI.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.constants.TransactionStatus;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.PPIMClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import com.axisbank.transit.transitCardAPI.constants.TxnType;
import com.axisbank.transit.transitCardAPI.exceptions.BlockedCardException;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.BlockCardDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardTransactionDTO;
import com.axisbank.transit.transitCardAPI.model.request.*;
import com.axisbank.transit.transitCardAPI.model.request.CustExistCheck.CustExistCheckRequest;
import com.axisbank.transit.transitCardAPI.model.request.availableLimit.AvailableLimitRequest;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationResp;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfo;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllTransactions.GetCardAllTransactions;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllTransactions.Paging;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.PopUpRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.RequestBody;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal.RequestBodyReversal;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal.TopupToPrepaidReversal;
import com.axisbank.transit.transitCardAPI.repository.CardDetailsRepository;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import in.juspay.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.TRANSIT_CARD_BINS;
import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.TRANSIT_CARD_VALID_REGISTRATION_STATUS;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.PAYMENT_GATEWAY;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.*;

@Slf4j
@Service
public class TransitCardTxnServiceImpl implements TransitCardTxnService {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private TransitCardClient transitCardClient;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    PaymentService paymentService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    CardDetailsRepository cardDetailsRepository;

    @Autowired
    GlobalConfigService globalConfigService;

    @Autowired
    ExploreService exploreService;

    @Autowired
    ExploreRepository exploreRepository;

    @Autowired
    private PPIMClient ppimClient;
    @Value("${topup.validate}")
    private boolean isTopupValidation;

    @Override
    public TopupToPrepaid getTopupToPrepaidRequest(String cardNumber, TopupRequest request) {
        return transitCardTxnRequest(cardNumber, request, OTS_TOPUP_VALUE, OCT_TOPUP_VALUE);
    }

    @Override
    public TopupToPrepaid getDebitFromTransitRequest(String cardNumber, TopupRequest request) {
        return transitCardTxnRequest(cardNumber, request, OTS_DEBIT_VALUE, OCT_DEBIT_VALUE);
    }

    private TopupToPrepaid transitCardTxnRequest(String cardNumber, TopupRequest request, String otsVal,
                                                 String octVal) {
        TopupToPrepaid topupToPrepaidReq = new TopupToPrepaid();
        RequestBody requestBody = new RequestBody();
        PopUpRequest popUpRequest = new PopUpRequest();
        popUpRequest.setAllowedProcessChannel(TransitCardAPIConstants.ALLOW_PROCESS_CHANNEL);
        popUpRequest.setBalanceType(TransitCardAPIConstants.BALANCE_TYPE);
        popUpRequest.setCardNo(cardNumber);
        popUpRequest.setInsertChannel(TransitCardAPIConstants.INSERT_CHANNEL);
        popUpRequest.setShowInStmt(TransitCardAPIConstants.SHOW_IN_STMT);
        popUpRequest.setTxnAmnt(request.getAmount());
        popUpRequest.setOts(otsVal);
        popUpRequest.setOtc(octVal);
        popUpRequest.setSrcRefId(request.getSrcRefId());
        requestBody.setPopUpRequest(popUpRequest);
        topupToPrepaidReq.setRequestBody(requestBody);
        return topupToPrepaidReq;
    }

    @Override
    public GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalanceInfoRequest(CardDetailsDAO cardDetailsDAO) throws Exception {
        GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalReq = new GetCardAllLimitAndBalanceInfoRequest();
        GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo = new GetCardAllLimitAndBalanceInfo();
        getCardAllLimitAndBalanceInfo.setCardNo(cardDetailsDAO.getCardNo());
//        TODO Set LimitType
        getCardAllLimitAndBalReq.setGetCardAllLimitAndBalanceInfo(getCardAllLimitAndBalanceInfo);
        return getCardAllLimitAndBalReq;
    }

    @Override
    public UpdateCardStatus getBlockCardRequest(CardDetailsDAO cardDetailsDAO) throws Exception {
        UpdateCardStatus updateCardStatusReq = new UpdateCardStatus();
        updateCardStatusReq.setCardNo(cardDetailsDAO.getCardNo());
//        TODO Update Card Status & SubStatus According to API request
        updateCardStatusReq.setDescription(TransitCardAPIConstants.DESCRIPTIONS);
        return updateCardStatusReq;
    }
    private UpdateCardStatus getBlockCardRequest(CardDetailsDAO cardDetailsDAO, String type, boolean block) throws Exception {
        UpdateCardStatus updateCardStatusReq = new UpdateCardStatus();
        if(!block)
            type="Active";
        Map<String, String> statusMap = getBlockStatusMap(type);
        updateCardStatusReq.setStatus(statusMap.get("status"));
        updateCardStatusReq.setSubStatus(statusMap.get("subStatus"));
        updateCardStatusReq.setCardNo(cardDetailsDAO.getCardNo());
        updateCardStatusReq.setDescription(TransitCardAPIConstants.DESCRIPTIONS);
        return updateCardStatusReq;
    }

    @Override
    public CardVerificationRespRequest getCardVerificationRequest(String mobileNo, String lastFourDigitCardNo) {
        CardVerificationResp cardVerificationResp = new CardVerificationResp(lastFourDigitCardNo, "", mobileNo);
        CardVerificationRespRequest cardVerificationRespRequest = new CardVerificationRespRequest();
        cardVerificationRespRequest.setCardVerificationResp(cardVerificationResp);
        return cardVerificationRespRequest;
    }

    @Override
    public AuthenticationDAO linkCardService(LinkCardRequest linkCardRequest) throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        String mobileNo = linkCardRequest.getMobileNo();

        CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
        if(cardDetailsDAO==null){
            cardDetailsDAO = new CardDetailsDAO();
        }
        if(!mobileNo.equalsIgnoreCase(authenticationDAO.getMobile())) {
            throw new Exception("Invalid Mobile Number. Can not allow user to link card");
        }
        if (linkCardRequest.getLastFourDigitCardNo() != null && !linkCardRequest.getLastFourDigitCardNo().equalsIgnoreCase("")) {
            CardVerificationResp cardVerificationResp =
                    new CardVerificationResp(linkCardRequest.getLastFourDigitCardNo(), "", linkCardRequest.getMobileNo());
            CardVerificationRespRequest cardVerificationRespRequest = new CardVerificationRespRequest();
            cardVerificationRespRequest.setCardVerificationResp(cardVerificationResp);
            JsonNode cardVerificationJson = transitCardClient.cardVerificationResp(cardVerificationRespRequest);
            JsonNode result = cardVerificationJson.get("CardVerificationResponse").get("CardVerificationResult");
            if (result.get("Result").asText().equalsIgnoreCase("Error")){
                throw new Exception(result.get("ReturnDescription").asText());
            }
            String cardNumber = result.get("CardList").get("string").asText();
            if(!verifyCardBin(cardNumber))
                throw new Exception("Card not allowed");
            cardDetailsDAO.setCardNo(cardNumber);
        } else {

            JSONObject jsonObject = fetchCustomerCardInfo(linkCardRequest.getMobileNo(),
                    linkCardRequest.getDob().toString(),linkCardRequest.getName(),linkCardRequest.getLastName());
            String cardNumber = jsonObject.getString("CardNo");
            String completeStatus = jsonObject.getString("CompleteStatus");
            log.debug("Card Status before link card: {}", completeStatus);
            if(!isRegistrationAllowed(completeStatus)) {
                if(completeStatus.equalsIgnoreCase(EXPIRED))
                    throw new Exception("Your card has been expired");
                throw new Exception("Your card has been permanently block");
            }
            if(!verifyCardBin(cardNumber))
                throw new Exception("Card not allowed");
            cardDetailsDAO.setCardNo(cardNumber);
        }
        cardDetailsDAO.setCardToken(CommonUtils.generateRandomString(30));
        cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);
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
    }

    @Override
    public List<TransitCardTransactionDTO> getTransitCardTransactions(int pageNo, int pageSize, String txnType)
            throws Exception{
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        if(authenticationDAO.getCardDetailsDAO() == null) throw new Exception("Card not linked");
        String cardToken = authenticationDAO.getCardDetailsDAO().getCardToken();
        Pageable paging = PageRequest.of(pageNo,pageSize,Sort.by("id").descending());
        List<TransactionDAO> transactionDAOS = transactionService.getTxnByTypeAndSp(ServiceProviderConstant.TRANSIT_CARD,
                cardToken, txnType, paging);
        List<TransitCardTransactionDTO> transitCardTransactionDTOS = new ArrayList<>();
        for (TransactionDAO transactionDAO:transactionDAOS){
            TransitCardTransactionDTO transitCardTransactionDTO = new TransitCardTransactionDTO();
            transitCardTransactionDTO.setAmount(String.valueOf(transactionDAO.getAmount()));
            transitCardTransactionDTO.setMethodPayement(transactionDAO.getPspPaymentMethod());
            transitCardTransactionDTO.setTypeOfPayement(transactionDAO.getPspPaymentMethodType());
            transitCardTransactionDTO.setPgRefId(transactionDAO.getPspTxnId());
            transitCardTransactionDTO.setPgTxnStatus(transactionDAO.getPspStatus());
            transitCardTransactionDTO.setTransitCardRefId(transactionDAO.getSpTxnId());
            transitCardTransactionDTO.setTransitCardTxnStatus(transactionDAO.getSpStatus());
            transitCardTransactionDTO.setTxnId(transactionDAO.getOrderId());
            transitCardTransactionDTO.setTxnStatus(transactionDAO.getFinalTxnStatus());
            transitCardTransactionDTO.setTxnType(transactionDAO.getTxnType());
            transitCardTransactionDTO.setUpdatedAt(transactionDAO.getUpdatedAt());
            transitCardTransactionDTOS.add(transitCardTransactionDTO);
        }
        return transitCardTransactionDTOS;
    }

    @Override
    public BlockCardDTO blockOrUnblockTransitCard(BlockCardRequest blockCardRequest) throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
        UpdateCardStatus updateCardStatus = getBlockCardRequest(cardDetailsDAO, blockCardRequest.getBlockType(), blockCardRequest.getBlock());
        JsonNode jsonNode = transitCardClient.updateCardStatus(updateCardStatus);
        String updateResultStatus =jsonNode.get("UpdateCardStatusResponse").get("UpdateCardStatusResult").get("Result").asText();
        BlockCardDTO blockCardDTO = new BlockCardDTO();
        if("Success".equals(updateResultStatus)) {
            blockCardDTO.setResult(true);
        } else {
            blockCardDTO.setResult(false);
        }
        blockCardDTO.setBlockType(blockCardRequest.getBlockType());
        return blockCardDTO;
    }

    @Override
    public TransitCardInfoDTO getTransitCardInfo() throws Exception {
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
        return getTransitCardInfo(cardDetailsDAO.getCardNo());
    }

    @Override
    public TransitCardInfoDTO getTransitCardInfo(String cardNumber) throws Exception {
        TransitCardInfoDTO transitCardInfoDTO =  new TransitCardInfoDTO();
        SWGetCardInfoResponse swGetCardInfoResponse =  new SWGetCardInfoResponse();
        swGetCardInfoResponse.setCardNo(cardNumber);
        JsonNode swGetCardInfoJsonRes = transitCardClient.swGetCardInfo(swGetCardInfoResponse);
        JsonNode cardInfoRes = swGetCardInfoJsonRes.get("GetCardInfoResponse").get("GetCardInfoResult");
        if (!cardInfoRes.get("Result").asText().equalsIgnoreCase("Success")){
            throw new Exception(cardInfoRes.get("ReturnDescription").asText());
        }
        log.info("Transit Get CardInfo API Response:{}", swGetCardInfoJsonRes);
        JsonNode cardDetailInfoJsonObj = cardInfoRes.get("CardInfoList").get("CardDetailInfo");
        String totalHostBalance = cardDetailInfoJsonObj.get("TtlHstBalCurr").asText();
        String totalChipBalance = cardDetailInfoJsonObj.get("ChipBalance").asText();
        String totalCardBalance;
        try{
            totalCardBalance = Double.toString(Double.parseDouble(totalHostBalance)+Double.parseDouble(totalChipBalance));
        } catch (Exception ex ){
            log.error("Failed to add host and chip balance");
            totalCardBalance=totalHostBalance;
        }
        String expiryDate = cardDetailInfoJsonObj.get("ExpiryDate").asText();
        String cardType = cardDetailInfoJsonObj.get("CardType").asText();
        String cardStatCode = cardDetailInfoJsonObj.get("CardStatCode").asText();
        String cardStatSubCode = cardDetailInfoJsonObj.get("CardSubStatCode").asText();
        String cardName = cardDetailInfoJsonObj.get("EmbossName").asText();
        String custNum = cardDetailInfoJsonObj.get("CustomerNo").asText();
        try{
            String barCodeNum = cardDetailInfoJsonObj.get("BarcodeNo").asText();
            transitCardInfoDTO.setBarcodeNum(barCodeNum);
        } catch (Exception ex){
            log.info("Failed to set barcode:{}", ex.getMessage());
        }
        transitCardInfoDTO.setCardNo(cardNumber);
        transitCardInfoDTO.setTotalCardBalance(totalCardBalance);
        transitCardInfoDTO.setTotalChipBalance(totalChipBalance);
        transitCardInfoDTO.setTotalHostBalance(totalHostBalance);
        transitCardInfoDTO.setCardStatCode(cardStatCode);
        transitCardInfoDTO.setCardType(cardType);
        transitCardInfoDTO.setCardStatSubCode(cardStatSubCode);
        transitCardInfoDTO.setEmbossName(cardName);
        transitCardInfoDTO.setCustomerNo(custNum);
        return transitCardInfoDTO;
    }

    @Override
    public Order createTopupRequest(AuthenticationDAO authenticationDAO, TopupRequest request) throws Exception {

        CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
        verifyBlockOperation(cardDetailsDAO.getCardNo());
        if(isTopupValidation){
            log.info("Validating Topup limit");
            Double totalBalance= getTotalCardBalance(authenticationDAO);
            String uniqueCustomerId = getUniqueCustIdFromPPIM(authenticationDAO.getMobile(), cardDetailsDAO.getCardNo());
            Double availableLimit = getAvailableLimitFromPPIM(uniqueCustomerId);
            if(availableLimit<(totalBalance+Double.parseDouble(request.getAmount())))
                throw new Exception("Your desired top-up limit exceeds the available limit "+availableLimit);
        }

        DAOUser daoUser = cardDetailsDAO.getAuthenticationDAO().getDaoUser();
        Order order = paymentService.createOrder(request, daoUser.getPgCustomerId(), TxnType.TOP_UP.toString());
        TransactionDAO transactionDAO = new TransactionDAO();

        transactionDAO.setAmount(Double.parseDouble(request.getAmount()));
        transactionDAO.setOrderId(order.getOrderId());
        transactionDAO.setTxnType(TxnType.TOP_UP.toString());
        transactionDAO.setFinalTxnStatus(TransactionStatus.INITIATED.toString());
        transactionDAO.setMerchantId(order.getMerchantId());
        transactionDAO.setPspStatus(order.getStatus());
        transactionDAO.setPspRefId(order.getCustomerId());
        transactionDAO.setPaymentServiceProvider(PAYMENT_GATEWAY);

        transactionDAO.setServiceProvider(ServiceProviderConstant.TRANSIT_CARD);
        transactionDAO.setSpRefId(cardDetailsDAO.getCardToken());
        transactionDAO.setSpStatus(TxnStatus.INITIATED.toString());
        transactionDAO.setAuthenticationDAO(authenticationDAO);
        transactionService.saveTxn(transactionDAO);
        return order;
    }

    @Override
    public String getTransitCardNo(String cardToken) throws Exception {
        return cardDetailsRepository.findByCardToken(cardToken).getCardNo();
    }

    @Override
    public void createTransitCardRefund(String cardToken, String txnId) throws Exception {
        String cardNum;
        try{
            cardNum = getTransitCardNo(cardToken);
        } catch (Exception ex){
            log.error("Failed to fetch card details: {}", ex.getMessage());
            throw new Exception("Invalid Card number");
        }
        TopupToPrepaidReversal topupToPrepaidReversal = new TopupToPrepaidReversal();
        RequestBodyReversal requestBodyReversal = new RequestBodyReversal();
        requestBodyReversal.setCardNo(cardNum);
        requestBodyReversal.setTxnReferanceId(txnId);
        requestBodyReversal.setInsertChannel("2");
        topupToPrepaidReversal.setRequestBodyReversal(requestBodyReversal);
        try{
            JsonNode refund = transitCardClient.topupToPrepaid_ReversalResponse(topupToPrepaidReversal);
            JsonNode refundData = refund.get("TopupToPrepaid_ReversalResponse").get("ResponseBody");
            JsonNode resultData = refundData.get("TopupToPrepaid_ReversalResult");
            String result = resultData.get("Result").asText();
            if (!result.equalsIgnoreCase("Success")) {
                throw new Exception("Failed to refund the amount");
            }
        } catch (Exception ex){
            log.error("Failed to reverse transaction: {}", ex.getMessage());
            throw new Exception("Failed to reverse transaction");
        }
    }

    private Map<String, String> getBlockStatusMap(String type){
        String status;
        String subStatus;
        switch (type.toUpperCase()){
            case "TEMPORARY":
                status = "G";
                subStatus = "N";
                break;
            case "PERMANENT":
                status="GG";
                subStatus="BL";
                break;
            case "EXPIRED":
                status="G";
                subStatus="Y";
                break;
            default:
                status = "N";
                subStatus = "N";
        }
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", status);
        statusMap.put("subStatus", subStatus);
        return statusMap;
    }

    @Override
    public String getBlockStatus(String status, String subStatus){
        String key = status.toUpperCase()+"-"+subStatus.toUpperCase();
        switch (key){
            case "G-N":
                return TEMP_BLOCK;
            case "GG-BL":
                return PERMANENT_BLOCK;
            case "N-N":
                return ACTIVE;
            case "G-Y":
                return EXPIRED;
            default:
                return PERMANENT_BLOCK;
        }
    }

    @Override
    public boolean verifyCardBin(String cardNumber){
        GlobalConfigDTO globalConfigDTO = globalConfigService.getGlobalConfig(TRANSIT_CARD_BINS, true);
        JsonNode node = globalConfigDTO.getJsonValue();
        List<String> bins= mapper.convertValue(node.get("cardBins"), new TypeReference<List<String>>(){});
        cardNumber = cardNumber.substring(0,6);
        return bins.contains(cardNumber);
    }

    @Override
    public JsonNode getCardAllTransactions(String take, String skip, String startDate, String endDate, String orderBy) throws Exception {
        log.info("Request Receive for fetching all Card Transaction from Proceed Axis Module");
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        JsonNode getCardAllTxnJsonRes;
        String cardNumber = authenticationDAO.getCardDetailsDAO().getCardNo();
        if(cardNumber ==null || cardNumber.length()==0) throw new Exception("Invalid Card Number");
        GetCardAllTransactions getCardAllTransactions = new GetCardAllTransactions();
        getCardAllTransactions.setCardNo(cardNumber);
        if (startDate!=null && endDate!=null) {
            getCardAllTransactions.setStartDate(startDate);
            getCardAllTransactions.setEndDate(endDate);
        }

        getCardAllTransactions.setPaging(new Paging(skip,take,"Desc"));
        try {
            getCardAllTxnJsonRes = transitCardClient.getCardAllTransactions(getCardAllTransactions);
        } catch (Exception ex) {
            log.error("Failed to call Proceed GetCardAllTransaction Api");
            throw ex;
        }
        JsonNode getCardAllTxnsResult = getCardAllTxnJsonRes.get("GetCardAllTransactionsResponse").get("GetCardAllTransactionsResult");
        if(!getCardAllTxnsResult.get("Result").asText().equalsIgnoreCase("Success")) throw new Exception("Failed to fetch Card all txns");
        return getCardAllTxnJsonRes;
    }

    @Override
    public void verifyBlockOperation(String cardNumber) throws BlockedCardException {

        TransitCardInfoDTO cardInfoDTO=null;
        try {
            cardInfoDTO = getTransitCardInfo(cardNumber);
        } catch (Exception ex) {
            log.info("Failed to fetch card data: {}", ex.getMessage());
            throw new BlockedCardException("Failed to fetch card details");
        }
        String status = cardInfoDTO.getCardStatCode();
        String subStatus = cardInfoDTO.getCardStatSubCode();
        String completeStatus = getBlockStatus(status,subStatus);
        if(!completeStatus.equalsIgnoreCase(ACTIVE))
            throw new BlockedCardException("This Operation is not allowed on blocked/expired card");
    }

    public Double getTotalCardBalance(AuthenticationDAO authenticationDAO) throws Exception {
         try {
             TransitCardInfoDTO cardInfoDTO = getTransitCardInfo(authenticationDAO.getCardDetailsDAO().getCardNo());
             return Double.valueOf(cardInfoDTO.getTotalCardBalance());
         } catch (Exception ex) {
             log.error("Failed to call Proceed GetCustomerCardInfo API");
             throw ex;
         }
    }

    @Override
    public String getUniqueCustIdFromPPIM(String mobileNumber, String cardNum) throws Exception {
        JsonNode custCheckJsonRes=null;
        CustExistCheckRequest custExistCheckRequest = new CustExistCheckRequest();
        custExistCheckRequest.setMobileNo(mobileNumber);
        custExistCheckRequest.setReferenceId(CommonUtils.generateRandInt(10));
        try {
            custCheckJsonRes = ppimClient.getReqForResForCustExistStatus(custExistCheckRequest);
            log.info("PPIM CustExistStatus API Res: {}",custCheckJsonRes);
        } catch (Exception ex) {
            log.error("Failed to call CustExistCheck API:{} ",ex.getMessage());
            throw ex;
        }
        if(!custCheckJsonRes.get("CustExistStatusResponse").get("Result").asText().equalsIgnoreCase("Success")) throw new Exception(custCheckJsonRes.get("CustExistStatusResponse").get("ErrorDescription").asText());
        JsonNode custDetails = custCheckJsonRes.get("CustomerDetails");
        JsonNode custData = custDetails.get("CustData");
        if (custData !=null){
            ArrayNode arrayNodes = (ArrayNode) custData;
            TransitCardInfoDTO cardInfoDTO = getTransitCardInfo(cardNum);
            for(JsonNode arrayNode:arrayNodes){
                if (arrayNode.get("CardNumber").asText().contains(cardInfoDTO.getBarcodeNum()))
                    return arrayNode.get("UniqueCustomerId").asText();
            }
        }
        try{
            return custDetails.get("UniqueCustomerId").asText();
        } catch (Exception ex){
            log.info("Failed To fetch Customer Info:{}", ex.getMessage());
            throw new Exception("Failed to fetch details");
        }
    }

    public Double getAvailableLimitFromPPIM(String uniqueCustomerId) throws Exception {

        JsonNode availableLimitJsonRes=null;
        AvailableLimitRequest availableLimitRequest = new AvailableLimitRequest();
        availableLimitRequest.setReferenceId(CommonUtils.generateRandInt(10));
        availableLimitRequest.setUniqueCustomerId(uniqueCustomerId);
        try {
            availableLimitJsonRes = ppimClient.getReqAndResForAvailableLimit(availableLimitRequest);
        } catch (Exception ex) {
            log.error("Failed to call PPIM AvailableLimit API :{}",ex.getMessage());
            throw ex;
        }
        if(!availableLimitJsonRes.get("AvailableLimitResponse").get("Result").asText().equalsIgnoreCase("Success"))
            throw new Exception(availableLimitJsonRes.get("AvailableLimitResponse").get("ErrorDescription").asText());
        return Double.valueOf(availableLimitJsonRes.get("CustomerLimit").get("AvailableLimit").asText());
    }
    @Override
    public boolean isRegistrationAllowed(String status){
        GlobalConfigDTO globalConfig = globalConfigService.getGlobalConfig(TRANSIT_CARD_VALID_REGISTRATION_STATUS, true);
        JsonNode node = globalConfig.getJsonValue();
        List<String> validTypes= mapper.convertValue(node.get("validTypes"), new TypeReference<List<String>>(){});
        return  validTypes.contains(status);
    }

    @Override
    public JSONObject fetchCustomerCardInfo(String mobile, String dob, String name, String lastName) throws Exception {
        JSONObject response = new JSONObject();
        GetCustomerCardInfo getCustomerCardInfo = new GetCustomerCardInfo();
        getCustomerCardInfo.setMobileNo(mobile);
        getCustomerCardInfo.setCustomerName(name);
        getCustomerCardInfo.setCustomerSurname(lastName);
        getCustomerCardInfo.setBirthDate(dob);
        JsonNode customerCardInfoJson = transitCardClient.getCustomerCardInfo(getCustomerCardInfo);
        JsonNode customerCardInfoResult = customerCardInfoJson.get("GetCustomerCardInfoResponse").get("GetCustomerCardInfoResult");
        if (customerCardInfoResult.get("Result").asText().equalsIgnoreCase("Error")){
            throw new Exception(customerCardInfoResult.get("ReturnDescription").asText());
        }

        String status=null,subStatus=null,cardNumber=null,completeStatus=null;
        String customerNumber = customerCardInfoResult.get("CustomerNo").asText();
        if(!customerCardInfoResult.isNull()) {
            JsonNode custCardListNode =customerCardInfoResult.get("CustomerCardList");
            JSONObject custCardListObj = new JSONObject(custCardListNode.toString());
            Object object = custCardListObj.get("CustomerCardList");

//                  This if condition will check if we are getting Array of CustomerCardList or single CustomerCardList Object
            if(object instanceof JSONArray) {
                JSONArray custCardArray = (JSONArray) object;
                for (int index =0;index<custCardArray.length();index++) {
                    JSONObject custCardInfoRes  = custCardArray.getJSONObject(index);
                    status = custCardInfoRes.getString("Status");
                    subStatus = custCardInfoRes.getString("SubStatus");
                    cardNumber = custCardInfoRes.getString("CardNo");
                    completeStatus = getBlockStatus(status,subStatus);
//                    If completeStatus equal to @ACTIVE value no need to search further
                    if(completeStatus.equalsIgnoreCase("ACTIVE")) {
                        break;
                    }
                }
            } else {
                JSONObject cusCardInfoObj = new JSONObject(object.toString());
                status = cusCardInfoObj.getString("Status");
                subStatus = cusCardInfoObj.getString("SubStatus");
                cardNumber = cusCardInfoObj.getString("CardNo");
                completeStatus = getBlockStatus(status,subStatus);
            }
        } else {
            throw new Exception("Failed to get CustomerCardList");
        }
        response.put("CompleteStatus",completeStatus);
        response.put("CardNo", cardNumber);
        response.put("CustomerNo", customerNumber);
        return response;
    }

    @Override
    public AuthenticationDAO linkReplacementCard(AuthenticationDAO authenticationDAO){
        LinkCardRequest req = new LinkCardRequest();
        DAOUser userDetails = authenticationDAO.getDaoUser();
        req.setMobileNo(authenticationDAO.getMobile());
        req.setDob(userDetails.getDob());
        req.setLastName(userDetails.getLastName());
        req.setName(userDetails.getFirstName());
        try{
            return linkCardService(req);
        } catch (Exception ex){
            log.error("Failed to link replacement card/ No Active card found");
            return null;
        }
    }

}
