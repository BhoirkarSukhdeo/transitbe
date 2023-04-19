package com.axisbank.transit.transitCardAPI.TransitCardClient;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCustomer.AdressInfo;
import com.axisbank.transit.transitCardAPI.model.request.updateCustomer.AdressList;
import com.axisbank.transit.transitCardAPI.model.request.updateCustomer.Customer;
import com.axisbank.transit.transitCardAPI.model.request.updateCustomer.UpdateCustomer;
import com.axisbank.transit.transitCardAPI.util.TransitUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientUtils.class})
public class TransitCardClientTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();

    @Mock
    private TransitUtils transitUtils;

    /*@Spy
    private final TransitCardClient springJunitService = new SpringService();*/

    @InjectMocks
    @Autowired
    TransitCardClient transitCardClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(transitCardClient, "transitDomain", "xyz");
    }

    @Test
    public void updateCustomerTest() throws Exception {
        UpdateCustomer updateCustomer = new UpdateCustomer();
        PowerMockito.mockStatic(HttpClientUtils.class);
        Customer customer = new Customer();
        AdressList adressList = new AdressList();
        AdressInfo adressInfo = new AdressInfo();
        adressInfo.toString();
        adressList.setAdressInfo(adressInfo);
        adressList.toString();
        customer.setAdressList(adressList);
        customer.setCustomerNo("123");
        customer.setBankingCustomerNo("abc");
        customer.setBankingCustomerNo("bankingCustomerNo");
        customer.setOneClickId("oneClickId");
        customer.setName("name");
        customer.setMidName("midName");
        customer.setSurname("surname");
        customer.setBirthDate("birthDate");
        customer.setFatherName("fatherName");
        customer.setMotherMaidenName("motherMaidenName");
        customer.setNationality("nationality");
        customer.setIssuedBy("issuedBy");
        customer.setPassportNo("passportNo");
        customer.setPassportIssuedBy("passportIssuedBy");
        customer.setPassportDateOfIssue("passportDateOfIssue");
        customer.setPassportDateOfExpire("passportDateOfExpire");
        customer.setPassportControlPeriod("passportControlPeriod");
        customer.setEmergencyContactPersonNameSurname("emergencyContactPersonNameSurname");
        customer.setResidenceCountryCode("residenceCountryCode");
        customer.setBirthCountry("birthCountry");
        customer.setBirthCity("birthCity");
        customer.setBirthPlace("birthPlace");
        customer.setEmail("email");
        customer.setCustomerType("customerType");
        customer.setCommunicationLanguage("communicationLanguage");
        customer.setSendSMS("sendSMS");
        customer.setSendEMail("sendEMail");
        customer.setMobileNo("mobileNo");
        customer.setPhoneHome("phoneHome");
        customer.setPhoneWork("phoneWork");
        customer.setPhoneWorkExtension("phoneWorkExtension");
        customer.setWorkPlace("workPlace");
        customer.setOccupation("occupation");
        customer.setTitle("title");
        customer.setAllocationDate("allocationDate");
        customer.setEmergencyPhoneFieldCode("emergencyPhoneFieldCode");
        customer.setEmergencyPhone("emergencyPhone");
        customer.setEmergencyPhoneExt("emergencyPhoneExt");
        customer.setMainBranchField("mainBranchField");
        customer.setGuaranteeFlag("guaranteeFlag");
        customer.setAssuranceType("assuranceType");
        customer.setNationalId("nationalId");
        customer.setCustomerGroup("customerGroup");
        customer.setCustodianNationalId("custodianNationalId");
        customer.setsMSOTPNo("sMSOTPNo");
        customer.setMotherName("motherName");
        customer.setParentName("parentName");
        customer.setParentNationalId("parentNationalId");
        customer.setParentDescription("parentDescription");
        customer.setChannelCode("channelCode");
        customer.setPictureFilePath("pictureFilePath");
        customer.setCustodian("custodian");
        customer.setFreeText1("freeText1");
        customer.setFreeText2("freeText2");
        customer.setFreeText3("freeText3");
        customer.setFreeText4("freeText4");
        customer.setFreeText5("freeText5");
        customer.setFreeText6("freeText6");
        customer.setFreeText7("freeText7");
        customer.setFreeText8("freeText8");
        customer.setFreeText9("freeText9");
        customer.setFreeText10("freeText10");
        customer.setFreeText11("freeText11");
        customer.setFreeText12("freeText12");
        customer.setFreeText13("freeText13");
        customer.setFreeText14("freeText14");
        customer.setFreeText15("freeText15");
        customer.setFreeText16("freeText16");
        customer.setFreeText17("freeText17");
        customer.setFreeText18("freeText18");
        customer.setFreeText19("freeText19");
        customer.setFreeText20("freeText20");
        customer.setFreeText21("freeText21");
        customer.setFreeText22("freeText22");
        customer.setFreeText23("freeText23");
        customer.setFreeText24("freeText24");
        customer.setFreeText25("freeText25");
        customer.setkYCStatus("kYCStatus");
        customer.setPanNumber("panNumber");
        customer.setAadharNo("aadharNo");

        customer.toString();
        updateCustomer.setCustomer(customer);
        PrepareTransitRequest transitRequest = new PrepareTransitRequest();
        when(transitUtils.getChecksumReqPayload(any(String.class),any(UpdateCustomer.class))).thenReturn(transitRequest);
        PowerMockito.when(HttpClientUtils.httpPostRequest("xyz", null)).thenReturn(BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "successful"));

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

        when(transitUtils.addTypeResponseChecksum(any(ResponseEntity.class))).thenReturn(getAllTxnJsonNode);
        transitCardClient.updateCustomer(updateCustomer);
    }

}
