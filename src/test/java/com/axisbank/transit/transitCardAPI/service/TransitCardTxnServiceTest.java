package com.axisbank.transit.transitCardAPI.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.explore.model.DAO.ExploreDAO;
import com.axisbank.transit.explore.repository.ExploreRepository;
import com.axisbank.transit.explore.service.ExploreService;
import com.axisbank.transit.payment.constants.Helper;
import com.axisbank.transit.payment.constants.OrderStatus;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.PPIMClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.BlockCardDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.request.*;
import com.axisbank.transit.transitCardAPI.model.request.CustExistCheck.CustExistCheckRequest;
import com.axisbank.transit.transitCardAPI.model.request.availableLimit.AvailableLimitRequest;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllTransactions.GetCardAllTransactions;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal.TopupToPrepaidReversal;
import com.axisbank.transit.transitCardAPI.repository.CardDetailsRepository;
import com.axisbank.transit.transitCardAPI.service.impl.TransitCardTxnServiceImpl;
import com.axisbank.transit.userDetails.constants.Gender;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.juspay.model.Customer;
import in.juspay.model.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.*;
import static com.axisbank.transit.kmrl.constant.Constants.TRANSIT_CARD_PAYMENT_METHOD;
import static com.axisbank.transit.payment.constants.PaymentServiceProviderConstant.TRANSIT_CARD;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.PERMANENT_BLOCK;
import static com.axisbank.transit.transitCardAPI.constants.TxnType.BOOK_TICKET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Customer.class, Order.class, ApplicationSetupData.class, BCrypt.class, Helper.class})
@PowerMockIgnore("javax.crypto.*")
public class TransitCardTxnServiceTest extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    CardDetailsDAO cardDetailsDAO;
    private AuthenticationDAO authenticationDAO;
    private DAOUser daoUser;
    TransactionDAO transactionDAO;
    static final Logger logger = LoggerFactory.getLogger(TransitCardTxnServiceTest.class);

    @InjectMocks
    @Autowired
    TransitCardTxnServiceImpl transitCardTxnService;

    @Mock
    private TransitCardClient transitCardClient;

    @Mock
    private AuthenticationRepository authenticationRepository;

    @Mock
    private UserUtil userUtil;

    @Mock
    PaymentService paymentService;

    @Mock
    TransactionService transactionService;

    @Mock
    CardDetailsRepository cardDetailsRepository;

    @Mock
    GlobalConfigService globalConfigService;

    @Mock
    ExploreService exploreService;

    @Mock
    ExploreRepository exploreRepository;

    @Mock
    PPIMClient ppimClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");
        ReflectionTestUtils.setField(transitCardTxnService, "isTopupValidation", true);

        authenticationDAO = new AuthenticationDAO();


        daoUser = new DAOUser();
        daoUser.setOccupation("SE");
        daoUser.setDob(LocalDate.of(1994, 11, 23));
        daoUser.setGender(Gender.MALE);
        daoUser.setPgCustomerId("123");

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);
        authenticationDAO.setDaoUser(daoUser);

        cardDetailsDAO = new CardDetailsDAO();
        cardDetailsDAO.setCardNo("123456789");
        cardDetailsDAO.setCardToken("1211112111");

        cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
        daoUser.setAuthenticationDAO(authenticationDAO);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);

        transactionDAO = new TransactionDAO();

        transactionDAO.setAmount(Double.parseDouble("20.3"));
        transactionDAO.setOrderId(CommonUtils.generateRandInt(10));
        transactionDAO.setTxnType(BOOK_TICKET.toString());
        transactionDAO.setPaymentServiceProvider(TRANSIT_CARD);
        transactionDAO.setServiceProvider(ServiceProviderConstant.KMRL);
        transactionDAO.setSpRefId("1232111");
        transactionDAO.setAuthenticationDAO(authenticationDAO);
        transactionDAO.setPspTxnId("121211111");
        transactionDAO.setPspPaymentMethodType(TRANSIT_CARD_PAYMENT_METHOD);
        transactionDAO.setPspPaymentMethod(TRANSIT_CARD_PAYMENT_METHOD);
        transactionDAO.setTxnCompletedOn(new Date(CommonUtils.getCurrentTimeMillis()));
        transactionDAO.setPspRefId(authenticationDAO.getCardDetailsDAO().getCardToken());
        transactionDAO.setPspStatus("success");
    }

    @Test
    public void getTopupToPrepaidRequestTest() {
        TopupRequest topupRequest = new TopupRequest();
        topupRequest.setAmount("23.4");
        topupRequest.setSrcRefId("123");
        TopupToPrepaid topupToPrepaid1 = transitCardTxnService.getTopupToPrepaidRequest("123456123456",topupRequest);
        Assert.assertNotNull(topupToPrepaid1);
    }

    @Test
    public void getCardAllLimitAndBalanceInfoRequestTest() throws Exception {
        GetCardAllLimitAndBalanceInfoRequest request = transitCardTxnService.getCardAllLimitAndBalanceInfoRequest(cardDetailsDAO);
        Assert.assertNotNull(request);
    }

    @Test
    public void getBlockCardRequestTest() throws Exception {
        UpdateCardStatus updateCardStatus1 = transitCardTxnService.getBlockCardRequest(cardDetailsDAO);
        Assert.assertNotNull(updateCardStatus1);
    }

    @Test
    public void getCardVerificationRequestTest() {
        String mobile ="9992399001";
        String lastFourDigitsOfCard ="2345";
        CardVerificationRespRequest cardVerificationRespRequest1 = transitCardTxnService.getCardVerificationRequest(mobile,lastFourDigitsOfCard);
        Assert.assertNotNull(cardVerificationRespRequest1);
    }

    @Test
    public void linkCardServiceTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String cardVerificationJson = "{\n" +
                "        \"CardVerificationResponse\": {\n" +
                "          \"CardVerificationResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": \"2\",\n" +
                "            \"ReturnDescription\": \"Successfully completed.\",\n" +
                "            \"ErrorDetail\": {},\n" +
                "            \"CardList\": {\n" +
                "              \"string\": \"6078572352607857\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }";
        JsonNode cardVerificationJsonNode = mapper.readTree(cardVerificationJson);
        when(transitCardClient.cardVerificationResp(any(CardVerificationRespRequest.class))).thenReturn(cardVerificationJsonNode);
        String cardBinsJson = "{\n" +
                "                \"cardBins\": [\n" +
                "                    \"607857\",\n" +
                "                    \"508980\"\n" +
                "                ]\n" +
                "            }";
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(TRANSIT_CARD_BINS);
        JsonNode cardBinsJsonNode = mapper.readTree(cardBinsJson);
        globalConfigDTO.setJsonValue(cardBinsJsonNode);
        when(globalConfigService.getGlobalConfig(TRANSIT_CARD_BINS, true)).thenReturn(globalConfigDTO);

        ExploreDAO exploreDAO = new ExploreDAO();
        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);
        doNothing().when(exploreService).mapExploreItems(any(AuthenticationDAO.class));

        String custCardInfoJson = "{\n" +
                "        \"GetCustomerCardInfoResponse\": {\n" +
                "          \"GetCustomerCardInfoResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": \"2\",\n" +
                "            \"ReturnDescription\": \"Success\",\n" +
                "            \"ErrorDetail\": {},\n" +
                "            \"CustomerCardList\": {\n" +
                "              \"CustomerCardList\": {\n" +
                "                \"CardNo\": \"5089800018375630\",\n" +
                "                \"EmbossName\": \"NIKHIL RODE\",\n" +
                "                \"BarcodeNo\": \"1010000003277\",\n" +
                "                \"Branch\": \"9000\",\n" +
                "                \"CardType\": \"N\",\n" +
                "                \"Status\": \"N\",\n" +
                "                \"SubStatus\": \"N\",\n" +
                "                \"Product\": \"1000000056\",\n" +
                "                \"TotalHostBalance\": \"769.30\",\n" +
                "                \"TotalChipBalance\": \"0\",\n" +
                "                \"TotalBalance\": \"769.30\",\n" +
                "                \"IssuanceDate\": \"19000101\",\n" +
                "                \"TotalLienBalance\": \"0\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"CustomerNo\": \"3610\",\n" +
                "            \"BankingCustomerNo\": {},\n" +
                "            \"MobileNo\": \"919823121212\"\n" +
                "          }\n" +
                "        }\n" +
                "      }";

        JsonNode custCardInfoJsonNode = mapper.readTree(custCardInfoJson);
        when(transitCardClient.getCustomerCardInfo(any(GetCustomerCardInfo.class))).thenReturn(custCardInfoJsonNode);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);

        LinkCardRequest linkCardRequest = new LinkCardRequest();
        linkCardRequest.setMobileNo("2233771199");
        linkCardRequest.setDob(daoUser.getDob());
        linkCardRequest.setLastFourDigitCardNo("7857");
        linkCardRequest.setName("abc");
        linkCardRequest.setLastName("xyz");
        AuthenticationDAO authenticationDAO2 = transitCardTxnService.linkCardService(linkCardRequest);
        Assert.assertNotNull(authenticationDAO2);
    }

    @Test
    public void getTransitCardTransactionsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        List<TransactionDAO> transactionDAOList = new ArrayList<>();
        transactionDAOList.add(transactionDAO);
        when(transactionService.getTxnByTypeAndSp(any(String.class), any(String.class), any(String.class), any(Pageable.class))).thenReturn(transactionDAOList);
        Assert.assertNotNull(transitCardTxnService.getTransitCardTransactions(0, 10, "transit"));
    }

    @Test
    public void blockOrUnblockTransitCardTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String updateCardStatusJson = "{\n" +
                "        \"UpdateCardStatusResponse\": {\n" +
                "          \"UpdateCardStatusResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": \"2\",\n" +
                "            \"ReturnDescription\": \"Successfully completed.\",\n" +
                "            \"ErrorDetail\": {}\n" +
                "          },\n" +
                "          \"NewCardNo\": {},\n" +
                "          \"NewExpireDate\": \"0\"\n" +
                "        }\n" +
                "      }";
        JsonNode updateCardStatusJsonNode = mapper.readTree(updateCardStatusJson);
        when(transitCardClient.updateCardStatus(any(UpdateCardStatus.class))).thenReturn(updateCardStatusJsonNode);

        BlockCardRequest blockCardRequest = new BlockCardRequest();
        blockCardRequest.setBlockType(PERMANENT_BLOCK);
        blockCardRequest.setBlock(true);
        Assert.assertNotNull(transitCardTxnService.blockOrUnblockTransitCard(blockCardRequest));
    }

    @Test
    public void createTransitCardRefundTest() throws Exception {
        when(cardDetailsRepository.findByCardToken(any(String.class))).thenReturn(cardDetailsDAO);
        String refundJson = "{\n" +
                "  \"TopupToPrepaid_ReversalResponse\": {\n" +
                "    \"ResponseBody\": {\n" +
                "      \"TopupToPrepaid_ReversalResult\": {\n" +
                "        \"Result\": \"Success\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode refundJsonNode = mapper.readTree(refundJson);
        when(transitCardClient.topupToPrepaid_ReversalResponse(any(TopupToPrepaidReversal.class))).thenReturn(refundJsonNode);
        transitCardTxnService.createTransitCardRefund("xyz", "txn123");
    }

    @Test
    public void getCardAllTransactionsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String getAllTxnJson = "{\n" +
                "  \"GetCardAllTransactionsResponse\": {\n" +
                "    \"GetCardAllTransactionsResult\": {\n" +
                "      \"Result\": \"Success\",\n" +
                "      \"ReturnCode\": \"2\",\n" +
                "      \"ReturnDescription\": \"Successfully completed.\",\n" +
                "      \"ErrorDetail\": {}\n" +
                "    },\n" +
                "    \"TotalCount\": \"5\"\n" +
                "  }\n" +
                "}";
        JsonNode getAllTxnJsonNode = mapper.readTree(getAllTxnJson);
        when(transitCardClient.getCardAllTransactions(any(GetCardAllTransactions.class))).thenReturn(getAllTxnJsonNode);
        Assert.assertNotNull(transitCardTxnService.getCardAllTransactions("0", "20", "20201223","20210223","Desc"));
    }

    @Test
    public void linkReplacementCardTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String cardBinsJson = "{\n" +
                "                \"cardBins\": [\n" +
                "                    \"607857\",\n" +
                "                    \"508980\"\n" +
                "                ]\n" +
                "            }";
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(TRANSIT_CARD_BINS);
        JsonNode cardBinsJsonNode = mapper.readTree(cardBinsJson);
        globalConfigDTO.setJsonValue(cardBinsJsonNode);
        when(globalConfigService.getGlobalConfig(TRANSIT_CARD_BINS, true)).thenReturn(globalConfigDTO);

        ExploreDAO exploreDAO = new ExploreDAO();
        List<ExploreDAO> exploreDAOList = new ArrayList<>();
        exploreDAOList.add(exploreDAO);
        when(exploreRepository.saveAll(authenticationDAO.getExploreDAOSet())).thenReturn(exploreDAOList);
        doNothing().when(exploreService).mapExploreItems(any(AuthenticationDAO.class));

        String custCardInfoJson = "{\n" +
                "        \"GetCustomerCardInfoResponse\": {\n" +
                "          \"GetCustomerCardInfoResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": \"2\",\n" +
                "            \"ReturnDescription\": \"Success\",\n" +
                "            \"ErrorDetail\": {},\n" +
                "            \"CustomerCardList\": {\n" +
                "              \"CustomerCardList\": {\n" +
                "                \"CardNo\": \"5089800018375630\",\n" +
                "                \"EmbossName\": \"NIKHIL RODE\",\n" +
                "                \"BarcodeNo\": \"1010000003277\",\n" +
                "                \"Branch\": \"9000\",\n" +
                "                \"CardType\": \"N\",\n" +
                "                \"Status\": \"N\",\n" +
                "                \"SubStatus\": \"N\",\n" +
                "                \"Product\": \"1000000056\",\n" +
                "                \"TotalHostBalance\": \"769.30\",\n" +
                "                \"TotalChipBalance\": \"0\",\n" +
                "                \"TotalBalance\": \"769.30\",\n" +
                "                \"IssuanceDate\": \"19000101\",\n" +
                "                \"TotalLienBalance\": \"0\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"CustomerNo\": \"3610\",\n" +
                "            \"BankingCustomerNo\": {},\n" +
                "            \"MobileNo\": \"919823121212\"\n" +
                "          }\n" +
                "        }\n" +
                "      }";

        JsonNode custCardInfoJsonNode = mapper.readTree(custCardInfoJson);
        when(transitCardClient.getCustomerCardInfo(any(GetCustomerCardInfo.class))).thenReturn(custCardInfoJsonNode);
        when(authenticationRepository.save(any(AuthenticationDAO.class))).thenReturn(authenticationDAO);


        String validRegistrationStatusJson = "{\n" +
                "                \"validTypes\": [\n" +
                "                    \"Temporary\",\n" +
                "                    \"Active\"\n" +
                "                ]\n" +
                "            }";

        GlobalConfigDTO globalConfigDTO2 = new GlobalConfigDTO();
        globalConfigDTO2.setJson(true);
        globalConfigDTO2.setValue(null);
        globalConfigDTO2.setKey(TRANSIT_CARD_VALID_REGISTRATION_STATUS);
        JsonNode validRegistrationStatusJsonNode = mapper.readTree(validRegistrationStatusJson);
        globalConfigDTO2.setJsonValue(validRegistrationStatusJsonNode);
        when(globalConfigService.getGlobalConfig(TRANSIT_CARD_VALID_REGISTRATION_STATUS, true)).thenReturn(globalConfigDTO2);

        LinkCardRequest linkCardRequest = new LinkCardRequest();
        linkCardRequest.setMobileNo("2233771199");
        linkCardRequest.setDob(daoUser.getDob());
        linkCardRequest.setLastFourDigitCardNo("7857");
        linkCardRequest.setName("abc");
        linkCardRequest.setLastName("xyz");

        Assert.assertNotNull(transitCardTxnService.linkReplacementCard(authenticationDAO));
    }

    @Test
    public void createTopUpRequestTest() throws Exception {
        Order order = new Order();
        order.setOrderId("123");
        order.setStatus(OrderStatus.CHARGED.toString());
        order.setCustomerId("cust123");
        order.setAmount(23.4);
        order.setPaymentMethod("card");
        order.setPaymentMethodType("online");

        when(paymentService.createOrder(any(TopupRequest.class), any(String.class), any(String.class))).thenReturn(order);
        when(transactionService.saveTxn(any(TransactionDAO.class))).thenReturn(transactionDAO);

        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String getCardInfoResJson = "{\n" +
                "        \"GetCardInfoResponse\": {\n" +
                "          \"GetCardInfoResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": \"2\",\n" +
                "            \"ReturnDescription\": \"Successfully completed.\",\n" +
                "            \"ErrorDetail\": {},\n" +
                "            \"CardInfoList\": {\n" +
                "              \"CardDetailInfo\": {\n" +
                "                \"CardDci\": \"Prepaid\",\n" +
                "                \"CardPointInfo\": {\n" +
                "                  \"EarnedPoint\": \"0\",\n" +
                "                  \"UsedPoint\": \"0\",\n" +
                "                  \"CampaignPoint\": \"0\",\n" +
                "                  \"AvaliablePoint\": \"0\"\n" +
                "                },\n" +
                "                \"IsBusiness\": \"false\",\n" +
                "                \"IssuingFee\": \"0\",\n" +
                "                \"TtlHstBalCurr\": \"769.30\",\n" +
                "                \"TtlCrdBalCurr\": \"0\",\n" +
                "                \"OrderAmount\": \"0\",\n" +
                "                \"ChipBalance\": \"10\",\n" +
                "                \"PinRetryCount\": \"0\",\n" +
                "                \"OfflinePin\": \"0\",\n" +
                "                \"EmbossDate\": \"19000101\",\n" +
                "                \"LogoCode\": \"44\",\n" +
                "                \"LastPinTryDate\": \"18.7.2017 00:00:00\",\n" +
                "                \"OldCardNo\": {},\n" +
                "                \"CardStatChangeDate\": \"20170719\",\n" +
                "                \"Atc\": \"18\",\n" +
                "                \"ExpiryDate\": \"201906\",\n" +
                "                \"RemainingLimit\": \"0\",\n" +
                "                \"CashLimitRatio\": {},\n" +
                "                \"Cvv2RetryCount\": \"0\",\n" +
                "                \"CardUpdateUser\": \"135699\",\n" +
                "                \"CardCancelReason\": {},\n" +
                "                \"PersonalizationDate\": \"1.1.190000:00:00\",\n" +
                "                \"EnrollmentNo\": \"112467\",\n" +
                "                \"FinancialType\": \"44\",\n" +
                "                \"LastTxnDate\": \"20170608\",\n" +
                "                \"LastTxnTime\": \"175412\",\n" +
                "                \"LastTxnAmount\": \"1330\",\n" +
                "                \"FirstUsageDate\": \"20170608\",\n" +
                "                \"CloseDate\": \"20170719\",\n" +
                "                \"RenewalDate\": \"19000101\",\n" +
                "                \"BankingCustomerNo\": \"2156\",\n" +
                "                \"CardType\": \"N\",\n" +
                "                \"CardNo\": \"6078572352968749\",\n" +
                "                \"CardStatCode\": \"N\",\n" +
                "                \"CardSubStatCode\": \"N\",\n" +
                "                \"CardStatDescription\": \"LostCustomer\",\n" +
                "                \"MainCardNo\": \"6078572352968749\",\n" +
                "                \"EmbossName\": \"NIKHIL RODE\",\n" +
                "                \"EmbossName2\": {},\n" +
                "                \"CardBrand\": \"Q\",\n" +
                "                \"CardProductName\": \"Oberthur UATtest\",\n" +
                "                \"CardProductId\": \"1000000028\",\n" +
                "                \"CardBranch\": \"200\",\n" +
                "                \"CustomerNo\": \"2156\",\n" +
                "                \"CardLevel\": \"M\",\n" +
                "                \"AccountNo\": \"6842515\",\n" +
                "                \"BarcodeNo\": {}\n" +
                "              }\n" +
                "            },\n" +
                "            \"CustomerMobilePhone\": \"919820816644\"\n" +
                "          }\n" +
                "        }\n" +
                "      }";
        JsonNode getCardInfoResJsonNode = mapper.readTree(getCardInfoResJson);
        when(transitCardClient.swGetCardInfo(any(SWGetCardInfoResponse.class))).thenReturn(getCardInfoResJsonNode);

        String custExistJson = "{\n" +
                "  \"CustExistStatusResponse\": {\n" +
                "    \"Result\": \"Success\",\n" +
                "    \"ReturnCode\": \"2\",\n" +
                "    \"ReturnDescription\": \"Successfully completed.\",\n" +
                "    \"ErrorDetail\": {}\n" +
                "  },\n" +
                "  \"CustomerDetails\": {\n" +
                "    \"CustData\": [\n" +
                "      {\n" +
                "        \"CardNumber\": \"123\",\n" +
                "        \"UniqueCustomerId\": \"123\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        JsonNode custExistJsonNode = mapper.readTree(custExistJson);
        when(ppimClient.getReqForResForCustExistStatus(any(CustExistCheckRequest.class))).thenReturn(custExistJsonNode);

        String availableLimitJson = "{\n" +
                "  \"AvailableLimitResponse\": {\n" +
                "    \"Result\": \"Success\",\n" +
                "    \"ReturnCode\": \"2\",\n" +
                "    \"ReturnDescription\": \"Successfully completed.\",\n" +
                "    \"ErrorDetail\": {}\n" +
                "  },\n" +
                "  \"CustomerLimit\": {\n" +
                "    \"AvailableLimit\": \"1000.5\"\n" +
                "  }\n" +
                "}";

        JsonNode availableLimitJsonNode = mapper.readTree(availableLimitJson);
        when(ppimClient.getReqAndResForAvailableLimit(any(AvailableLimitRequest.class))).thenReturn(availableLimitJsonNode);

        TopupRequest topupRequest = new TopupRequest();
        topupRequest.setAmount("23.4");
        Assert.assertNotNull(transitCardTxnService.createTopupRequest(authenticationDAO, topupRequest));
    }
}
