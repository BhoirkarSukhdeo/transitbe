package com.axisbank.transit.transitCardAPI.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.config.ApplicationSetupData;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.DTO.GlobalConfigDTO;
import com.axisbank.transit.core.service.GlobalConfigService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.CardLimitsClient;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DAO.CardLimitDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.UpdateCardTxnLimitDTO;
import com.axisbank.transit.transitCardAPI.model.request.GetCustomer;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfo;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.UpdateCardLimitRequestRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.UpdateCardOfflineAmountRequest;
import com.axisbank.transit.transitCardAPI.repository.CardLimitDetailsRepository;
import com.axisbank.transit.transitCardAPI.service.impl.TransitCardLimitServiceImpl;
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

import static com.axisbank.transit.core.shared.constants.GlobalConfigConstants.CARD_LIMIT_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationSetupData.class})
@PowerMockIgnore("javax.crypto.*")
public class TransitCardLimitServiceTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    CardDetailsDAO cardDetailsDAO;
    private AuthenticationDAO authenticationDAO;
    JsonNode getCustomerJsonNode;

    @InjectMocks
    @Autowired
    TransitCardLimitServiceImpl transitCardLimitService;

    @Mock
    private TransitCardClient transitCardClient;

    @Mock
    private UserUtil userUtil;

    @Mock
    private TransitCardTxnService transitCardTxnService;

    @Mock
    private GlobalConfigService globalConfigService;

    @Mock
    private CardLimitsClient cardLimitsClient;

    @Mock
    CardLimitDetailsRepository cardLimitDetailsRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ApplicationSetupData.class);
        PowerMockito.when(ApplicationSetupData.getCardSecretKey()).thenReturn("U75m30vuhpikWy2Z");
        //ReflectionTestUtils.setField(transitCardTxnService, "isTopupValidation", true);

        authenticationDAO = new AuthenticationDAO();

        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setOtpVerification(false);

        cardDetailsDAO = new CardDetailsDAO();
        cardDetailsDAO.setCardNo("123456789");
        cardDetailsDAO.setCardToken("1211112111");

        cardDetailsDAO.setAuthenticationDAO(authenticationDAO);
        authenticationDAO.setCardDetailsDAO(cardDetailsDAO);

        String getCustomerJson = "{\n" +
                "        \"GetCustomerResponse\": {\n" +
                "          \"GetCustomerResult\": {\n" +
                "            \"Result\": \"Success\",\n" +
                "            \"ReturnCode\": 2,\n" +
                "            \"ReturnDescription\": \"Success\",\n" +
                "            \"ErrorDetail\": \"\",\n" +
                "            \"Customer\": {\n" +
                "              \"CustomerNo\": \"3544\",\n" +
                "              \"OneClickId\": \"\",\n" +
                "              \"Name\": \"HARI\",\n" +
                "              \"MidName\": \"\",\n" +
                "              \"Surname\": \"PRASAD\",\n" +
                "              \"BirthDate\": \"19890112\",\n" +
                "              \"FatherName\": \"\",\n" +
                "              \"MotherMaidenName\": \"testa\",\n" +
                "              \"Nationality\": \"IN\",\n" +
                "              \"PassportNo\": \"\",\n" +
                "              \"PassportIssuedBy\": \"\",\n" +
                "              \"PassportDateOfIssue\": \"19000101\",\n" +
                "              \"PassportDateOfExpire\": \"19000101\",\n" +
                "              \"PassportControlPeriod\": 0,\n" +
                "              \"EmergencyContactPersonNameSurname\": \"\",\n" +
                "              \"ResidenceCountryCode\": \"\",\n" +
                "              \"BirthCity\": \"\",\n" +
                "              \"BirthPlace\": \"\",\n" +
                "              \"Email\": \"ani@gmail.com\",\n" +
                "              \"CustomerType\": \"N\",\n" +
                "              \"Gender\": \"M\",\n" +
                "              \"CommunicationLanguage\": \"EN\",\n" +
                "              \"SendSMS\": \"\",\n" +
                "              \"SendEMail\": \"\",\n" +
                "              \"MobileNo\": \"919821001144\",\n" +
                "              \"WorkPlace\": \"\",\n" +
                "              \"Occupation\": \"salary\",\n" +
                "              \"Title\": \"MR\",\n" +
                "              \"EmergencyPhoneFieldCode\": \"\",\n" +
                "              \"EmergencyPhone\": \"\",\n" +
                "              \"EmergencyPhoneExt\": \"\",\n" +
                "              \"MainBranchField\": 200,\n" +
                "              \"GuaranteeFlag\": \"N\",\n" +
                "              \"NationalId\": \"IN\",\n" +
                "              \"CustomerGroup\": \"1\",\n" +
                "              \"SMSOTPNo\": \"\",\n" +
                "              \"MotherName\": \"\",\n" +
                "              \"ParentName\": \"\",\n" +
                "              \"PictureFilePath\": \"\",\n" +
                "              \"AdressList\": {\n" +
                "                \"AdressInfo\": {\n" +
                "                  \"AdressIdx\": 1,\n" +
                "                  \"AdressType\": \"1\",\n" +
                "                  \"Address1\": \"airoli\",\n" +
                "                  \"Address2\": \"\",\n" +
                "                  \"Address3\": \"\",\n" +
                "                  \"AddressCity\": \"Bangalore Rural\",\n" +
                "                  \"AddressCityCode\": \"IN~410\",\n" +
                "                  \"AddressTown\": \"\",\n" +
                "                  \"AddressTownCode\": \"\",\n" +
                "                  \"AddressCountry\": \"IN\",\n" +
                "                  \"AddressZipCode\": \"400602\",\n" +
                "                  \"AddressStateCode\": \"23\",\n" +
                "                  \"AddressState\": \"Karnataka\"\n" +
                "                }\n" +
                "              },\n" +
                "              \"Custodian\": \"\",\n" +
                "              \"FreeText1\": \"\",\n" +
                "              \"FreeText2\": \"\",\n" +
                "              \"FreeText3\": \"\",\n" +
                "              \"FreeText4\": \"\",\n" +
                "              \"FreeText5\": \"\",\n" +
                "              \"FreeText6\": \"N\",\n" +
                "              \"FreeText7\": \"\",\n" +
                "              \"FreeText8\": \"\",\n" +
                "              \"FreeText9\": \"\",\n" +
                "              \"FreeText10\": \"\",\n" +
                "              \"FreeText11\": \"\",\n" +
                "              \"FreeText12\": \"\",\n" +
                "              \"FreeText13\": \"\",\n" +
                "              \"FreeText14\": \"\",\n" +
                "              \"FreeText15\": \"\",\n" +
                "              \"FreeText16\": \"\",\n" +
                "              \"FreeText17\": \"\",\n" +
                "              \"FreeText18\": \"\",\n" +
                "              \"FreeText19\": \"\",\n" +
                "              \"FreeText20\": \"\",\n" +
                "              \"FreeText21\": \"aqn11241\",\n" +
                "              \"FreeText22\": \"\",\n" +
                "              \"FreeText23\": \"\",\n" +
                "              \"FreeText24\": \"\",\n" +
                "              \"FreeText25\": \"\",\n" +
                "              \"KYCStatus\": \"Y1\",\n" +
                "              \"IsOtpCustomer\": false\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }";
        getCustomerJsonNode = mapper.readTree(getCustomerJson);
    }

    @Test
    public void getLimitsTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalReq = new GetCardAllLimitAndBalanceInfoRequest();
        GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo = new GetCardAllLimitAndBalanceInfo();
        getCardAllLimitAndBalanceInfo.setCardNo(cardDetailsDAO.getCardNo());
//        TODO Set LimitType
        getCardAllLimitAndBalReq.setGetCardAllLimitAndBalanceInfo(getCardAllLimitAndBalanceInfo);
        when(transitCardTxnService.getCardAllLimitAndBalanceInfoRequest(any(CardDetailsDAO.class))).thenReturn(getCardAllLimitAndBalReq);
        String getAllLimitsJson = "{\"GetAllLimitAndBalanceInfoResponse\":{\"GetAllLimitAndBalanceInfoResult\":{\"Result\":\"Success\",\"ReturnCode\":\"0\",\"ReturnDescription\":{},\"ErrorDetail\":{},\"AllLimitsDictionary\":{\"CardLimitsSummary\":[{\"LimtTypesObj\":{\"LimitType\":\"T\",\"LimitDescription\":\"TR:=SatışLimiti;;EN:=Sales Limit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"T\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"0\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"0\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"2000\",\"MaxDailyAmount\":\"999999\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"1500\",\"MaxDailyAmount\":\"5000\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"5000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"20\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"450\",\"MaxYearlyCount\":\"3650\"},{\"LimitProfileId\":\"222\",\"LimitProfileDesc\":\"Customer Group SalesLimit\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"20000\",\"MaxDailyAmount\":\"20000\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"5000\",\"RealWeeklyAmount\":\"999999\",\"RealMonthlyAmount\":\"5000\",\"RealYearlyAmount\":\"60000\",\"RealDailyCount\":\"20\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"450\",\"RealYearlyCount\":\"3650\",\"RealSingleAmount\":\"5000\"}},{\"LimtTypesObj\":{\"LimitType\":\"I\",\"LimitDescription\":\"TR:=Maksimum Tutar Limiti;;EN:=Maximum BalanceLimit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"I\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"0\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"0\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"5000\",\"MaxDailyAmount\":\"10000\",\"MaxWeeklyAmount\":\"10000\",\"MaxMonthlyAmount\":\"10000\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"false\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"false\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"2000\",\"MaxDailyAmount\":\"0\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"0\",\"MaxYearlyAmount\":\"0\",\"MaxDailyCount\":\"0\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"0\",\"MaxYearlyCount\":\"0\"},{\"LimitProfileId\":\"222\",\"LimitProfileDesc\":\"Customer Group Maximum BalanceLimit\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"false\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"false\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"20000\",\"MaxDailyAmount\":\"999999\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"10000\",\"RealWeeklyAmount\":\"10000\",\"RealMonthlyAmount\":\"10000\",\"RealYearlyAmount\":\"999999\",\"RealDailyCount\":\"999999\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"999999\",\"RealYearlyCount\":\"999999\",\"RealSingleAmount\":\"5000\"}},{\"LimtTypesObj\":{\"LimitType\":\"E\",\"LimitDescription\":\"TR:=Yükleme Limiti;;EN:=Load Limit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"E\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"100\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"1\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"E\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"3000\",\"MaxDailyAmount\":\"10000\",\"MaxWeeklyAmount\":\"10000\",\"MaxMonthlyAmount\":\"10000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"E\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"1500\",\"MaxDailyAmount\":\"5000\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"5000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"20\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"50\",\"MaxYearlyCount\":\"500\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"5000\",\"RealWeeklyAmount\":\"10000\",\"RealMonthlyAmount\":\"5000\",\"RealYearlyAmount\":\"60000\",\"RealDailyCount\":\"20\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"50\",\"RealYearlyCount\":\"500\",\"RealSingleAmount\":\"5000\"}}]},\"BalanceDetailObj\":{\"TotalHostBalance\":\"66.25\",\"TotalChipBalance\":\"0\",\"CurrencyCode\":\"356\"}}}}";
        JsonNode getAllLimitsJsonNode = mapper.readTree(getAllLimitsJson);
        when(transitCardClient.getCardAllLimitAndBalanceInfo(any(GetCardAllLimitAndBalanceInfoRequest.class))).thenReturn(getAllLimitsJsonNode);
        String configLimitsJson = "{\n" +
                "                \"totalCardBalanceLimitWithFullKYC\": \"30000\",\n" +
                "                \"totalCardBalanceLimitWithMinKYC\": \"5000\",\n" +
                "                \"chipBalanceMinLimit\": \"500\",\n" +
                "                \"chipBalanceMaxLimit\": \"2000\"\n" +
                "            }";
        JsonNode configLimitsJsonNode = mapper.readTree(configLimitsJson);
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(CARD_LIMIT_DETAILS);
        globalConfigDTO.setJsonValue(configLimitsJsonNode);
        when(globalConfigService.getGlobalConfig(CARD_LIMIT_DETAILS, true)).thenReturn(globalConfigDTO);

        TransitCardInfoDTO cardInfoDTO = new TransitCardInfoDTO();
        cardInfoDTO.setCustomerNo("122345");
        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(cardInfoDTO);
        when(transitCardClient.getCustomer(any(GetCustomer.class))).thenReturn(getCustomerJsonNode);
        Assert.assertNotNull(transitCardLimitService.getLimits());
    }

    @Test
    public void updateCardChipLimitTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        String configLimitsJson = "{\n" +
                "                \"totalCardBalanceLimitWithFullKYC\": \"30000\",\n" +
                "                \"totalCardBalanceLimitWithMinKYC\": \"5000\",\n" +
                "                \"chipBalanceMinLimit\": \"500\",\n" +
                "                \"chipBalanceMaxLimit\": \"2000\"\n" +
                "            }";
        JsonNode configLimitsJsonNode = mapper.readTree(configLimitsJson);
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setJson(true);
        globalConfigDTO.setValue(null);
        globalConfigDTO.setKey(CARD_LIMIT_DETAILS);
        globalConfigDTO.setJsonValue(configLimitsJsonNode);
        when(globalConfigService.getGlobalConfig(CARD_LIMIT_DETAILS, true)).thenReturn(globalConfigDTO);

        String updateLimitResJson = "{\n" +
                "      \"updateCardOfflineAmountResponse\": {\n" +
                "        \"responseBody\": {\n" +
                "          \"status\": \"Success\",\n" +
                "          \"responseCode\": 2,\n" +
                "          \"responseDescription\": \"Successfully completed.\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        JsonNode updateLimitResJsonNode = mapper.readTree(updateLimitResJson);
        when(cardLimitsClient.updateCardOfflineAmount(any(UpdateCardOfflineAmountRequest.class))).thenReturn(updateLimitResJsonNode);
        CardLimitDetailsDAO cardLimitDetailsDAO = new CardLimitDetailsDAO();
        when(cardLimitDetailsRepository.save(cardLimitDetailsDAO)).thenReturn(cardLimitDetailsDAO);
        TransitCardInfoDTO cardInfoDTO = new TransitCardInfoDTO();
        cardInfoDTO.setCustomerNo("122345");
        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(cardInfoDTO);
        when(transitCardClient.getCustomer(any(GetCustomer.class))).thenReturn(getCustomerJsonNode);
        transitCardLimitService.updateCardChipLimit(45);
    }

    @Test
    public void updateCardTxnLimitTest() throws Exception {
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalReq = new GetCardAllLimitAndBalanceInfoRequest();
        GetCardAllLimitAndBalanceInfo getCardAllLimitAndBalanceInfo = new GetCardAllLimitAndBalanceInfo();
        getCardAllLimitAndBalanceInfo.setCardNo(cardDetailsDAO.getCardNo());
//        TODO Set LimitType
        getCardAllLimitAndBalReq.setGetCardAllLimitAndBalanceInfo(getCardAllLimitAndBalanceInfo);
        when(transitCardTxnService.getCardAllLimitAndBalanceInfoRequest(any(CardDetailsDAO.class))).thenReturn(getCardAllLimitAndBalReq);
        String getAllLimitsJson = "{\"GetAllLimitAndBalanceInfoResponse\":{\"GetAllLimitAndBalanceInfoResult\":{\"Result\":\"Success\",\"ReturnCode\":\"0\",\"ReturnDescription\":{},\"ErrorDetail\":{},\"AllLimitsDictionary\":{\"CardLimitsSummary\":[{\"LimtTypesObj\":{\"LimitType\":\"T\",\"LimitDescription\":\"TR:=SatışLimiti;;EN:=Sales Limit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"T\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"0\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"0\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"2000\",\"MaxDailyAmount\":\"999999\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"1500\",\"MaxDailyAmount\":\"5000\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"5000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"20\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"450\",\"MaxYearlyCount\":\"3650\"},{\"LimitProfileId\":\"222\",\"LimitProfileDesc\":\"Customer Group SalesLimit\",\"LimitType\":\"T\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"20000\",\"MaxDailyAmount\":\"20000\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"5000\",\"RealWeeklyAmount\":\"999999\",\"RealMonthlyAmount\":\"5000\",\"RealYearlyAmount\":\"60000\",\"RealDailyCount\":\"20\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"450\",\"RealYearlyCount\":\"3650\",\"RealSingleAmount\":\"5000\"}},{\"LimtTypesObj\":{\"LimitType\":\"I\",\"LimitDescription\":\"TR:=Maksimum Tutar Limiti;;EN:=Maximum BalanceLimit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"I\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"0\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"0\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"5000\",\"MaxDailyAmount\":\"10000\",\"MaxWeeklyAmount\":\"10000\",\"MaxMonthlyAmount\":\"10000\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"false\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"false\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"2000\",\"MaxDailyAmount\":\"0\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"0\",\"MaxYearlyAmount\":\"0\",\"MaxDailyCount\":\"0\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"0\",\"MaxYearlyCount\":\"0\"},{\"LimitProfileId\":\"222\",\"LimitProfileDesc\":\"Customer Group Maximum BalanceLimit\",\"LimitType\":\"I\",\"CheckDailyAmount\":\"false\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"false\",\"CheckYearlyAmount\":\"false\",\"CheckDailyCount\":\"false\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"false\",\"CheckYearlyCount\":\"false\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"20000\",\"MaxDailyAmount\":\"999999\",\"MaxWeeklyAmount\":\"999999\",\"MaxMonthlyAmount\":\"999999\",\"MaxYearlyAmount\":\"999999\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"10000\",\"RealWeeklyAmount\":\"10000\",\"RealMonthlyAmount\":\"10000\",\"RealYearlyAmount\":\"999999\",\"RealDailyCount\":\"999999\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"999999\",\"RealYearlyCount\":\"999999\",\"RealSingleAmount\":\"5000\"}},{\"LimtTypesObj\":{\"LimitType\":\"E\",\"LimitDescription\":\"TR:=Yükleme Limiti;;EN:=Load Limit\"},\"UsedLimitDetailObj\":{\"LimitType\":\"E\",\"UsedDailyAmount\":\"0\",\"UsedWeeklyAmount\":\"0\",\"UsedMonthlyAmount\":\"0\",\"UsedYearlyAmount\":\"100\",\"UsedDailyCount\":\"0\",\"UsedWeeklyCount\":\"0\",\"UsedMonthlyCount\":\"0\",\"UsedYearlyCount\":\"1\"},\"LimitDetailList\":{\"LimitDetail\":[{\"LimitProfileId\":\"610\",\"LimitProfileDesc\":\"Product\",\"LimitType\":\"E\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"true\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"true\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"3000\",\"MaxDailyAmount\":\"10000\",\"MaxWeeklyAmount\":\"10000\",\"MaxMonthlyAmount\":\"10000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"999999\",\"MaxWeeklyCount\":\"999999\",\"MaxMonthlyCount\":\"999999\",\"MaxYearlyCount\":\"999999\"},{\"LimitProfileId\":\"106\",\"LimitProfileDesc\":\"Card\",\"LimitType\":\"E\",\"CheckDailyAmount\":\"true\",\"CheckWeeklyAmount\":\"false\",\"CheckMonthlyAmount\":\"true\",\"CheckYearlyAmount\":\"true\",\"CheckDailyCount\":\"true\",\"CheckWeeklyCount\":\"false\",\"CheckMonthlyCount\":\"true\",\"CheckYearlyCount\":\"true\",\"CheckSingleAmount\":\"true\",\"MaxSingleAmount\":\"1500\",\"MaxDailyAmount\":\"5000\",\"MaxWeeklyAmount\":\"0\",\"MaxMonthlyAmount\":\"5000\",\"MaxYearlyAmount\":\"60000\",\"MaxDailyCount\":\"20\",\"MaxWeeklyCount\":\"0\",\"MaxMonthlyCount\":\"50\",\"MaxYearlyCount\":\"500\"}]},\"RealCardLimitDetailObj\":{\"RealDailyAmount\":\"5000\",\"RealWeeklyAmount\":\"10000\",\"RealMonthlyAmount\":\"5000\",\"RealYearlyAmount\":\"60000\",\"RealDailyCount\":\"20\",\"RealWeeklyCount\":\"999999\",\"RealMonthlyCount\":\"50\",\"RealYearlyCount\":\"500\",\"RealSingleAmount\":\"5000\"}}]},\"BalanceDetailObj\":{\"TotalHostBalance\":\"66.25\",\"TotalChipBalance\":\"0\",\"CurrencyCode\":\"356\"}}}}";
        JsonNode getAllLimitsJsonNode = mapper.readTree(getAllLimitsJson);
        when(transitCardClient.getCardAllLimitAndBalanceInfo(any(GetCardAllLimitAndBalanceInfoRequest.class))).thenReturn(getAllLimitsJsonNode);
        CardLimitDetailsDAO cardLimitDetailsDAO = new CardLimitDetailsDAO();
        when(cardLimitDetailsRepository.save(cardLimitDetailsDAO)).thenReturn(cardLimitDetailsDAO);
        String updateLimitResJson = "{\n" +
                "      \"updateCardLimitResponse\": {\n" +
                "        \"responseBody\": {\n" +
                "          \"status\": \"Success\",\n" +
                "          \"responseCode\": 2,\n" +
                "          \"responseDescription\": \"Successfully completed.\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        JsonNode updateLimitResJsonNode = mapper.readTree(updateLimitResJson);
        when(cardLimitsClient.updateCardLimit(any(UpdateCardLimitRequestRequest.class))).thenReturn(updateLimitResJsonNode);
        TransitCardInfoDTO cardInfoDTO = new TransitCardInfoDTO();
        cardInfoDTO.setCustomerNo("122345");
        when(transitCardTxnService.getTransitCardInfo(any(String.class))).thenReturn(cardInfoDTO);
        when(transitCardClient.getCustomer(any(GetCustomer.class))).thenReturn(getCustomerJsonNode);
        UpdateCardTxnLimitDTO updateCardTxnLimitDTO = new UpdateCardTxnLimitDTO();
        updateCardTxnLimitDTO.setLimitType("T");
        updateCardTxnLimitDTO.setAmount(34);
        updateCardTxnLimitDTO.setEnabled(true);
        transitCardLimitService.updateCardTxnLimit(updateCardTxnLimitDTO);
    }
}
