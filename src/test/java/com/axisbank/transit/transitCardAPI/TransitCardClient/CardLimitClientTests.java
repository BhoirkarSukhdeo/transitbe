package com.axisbank.transit.transitCardAPI.TransitCardClient;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.UpdateCardLimitRequestRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.RequestBody;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.UpdateCardOfflineAmountRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.util.CardLimitsUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientUtils.class})
public class CardLimitClientTests extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    @Autowired
    CardLimitsClient cardLimitsClient;

    @Mock
    private CardLimitsUtils cardLimitsUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(HttpClientUtils.class);
        ReflectionTestUtils.setField(cardLimitsClient, "updateCardLimitUrl", "xyz");
        ReflectionTestUtils.setField(cardLimitsClient, "updateCardOfflineAmountUrl", "xyz");
    }

    @Test
    public void updateCardOfflineAmountTest() throws Exception {
        PrepareTransitRequest prepareTransitRequest = new PrepareTransitRequest();
        Mockito.when(cardLimitsUtils.getEncryptedReqPayload(any(String.class), any(String.class))).thenReturn(prepareTransitRequest);
        String body = "{\n" +
                "    \"response\": {\n" +
                "      \"Header\": {\n" +
                "        \"AuthSessionInfo\": {\n" +
                "          \"AuthUserID\": \"135699\",\n" +
                "          \"AuthPassword\": \"z4PhNX7vuL3xVChQ1m2AB9Yg5AULVxXcg/SpIdNs6c5H0NE8XYXysP+DGNKHfuwvY7kxvUdBeoGlODJ6+SfaPg==\",\n" +
                "          \"UserCode\": {},\n" +
                "          \"Language\": \"EN\",\n" +
                "          \"MbrId\": \"1\"\n" +
                "        },\n" +
                "        \"subHeader\": {\n" +
                "          \"RequestUUID\": \"1\",\n" +
                "          \"ServiceRequestId\": \"?\",\n" +
                "          \"ServiceRequestVersion\": \"1.0\",\n" +
                "          \"ChannelId\": \"ESB\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"body\": {\n" +
                "        \"TopupToPrepaidResponse\": {\n" +
                "          \"encryptedResponse\": \"wHXMI+91SARsu/z9LmaQoGcKgPA9oWvtsNIhdbwB31uBcbgfYr0vVkhPD59qtgdIO2VSfx/96GhkCQ4Y9+XfqboWnrkIoFTAu2zQ42QL0+z0mHFWCWkreRSzimQYAGBtQLt2iDN7vUQWQyRQNx/TqXSdZUH/z4QBpggknF7V7S0YAYdixtmnCQ31wzmL5Bz05UtYz6NwQNRVLatRoqDcdSId3cgzSC2JAsZN6zQnc9pa/6IajUNMI8bSF/nnAlqhRMPZ/tb/EPK7dtoEY5XGoOK1UZZzMXEZFAq7maX06v4jbrC5jZi7IaEW95Um1TcvWmoWSXrM7j7+h2Z2OtFabldV3hG2H+bnNxyW1GfROTmOuv1/\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted().body(body);
        PowerMockito.when(HttpClientUtils.httpPostRequest(any(String.class),any(PrepareTransitRequest.class))).thenReturn(responseEntity);
        JsonNode actualObj = mapper.readTree(body);
        Mockito.when(cardLimitsUtils.addTypeResponse(any(ResponseEntity.class), any(String.class))).thenReturn(actualObj);
        UpdateCardOfflineAmountRequest updateCardOfflineAmountRequest = new UpdateCardOfflineAmountRequest();
        RequestBody requestBody = new RequestBody();
        requestBody.setAmount("23");
        requestBody.setCardNumber("12333");
        requestBody.setLanguage("EN");
        requestBody.setPassword("hhwgh");
        requestBody.setMbrId("1");
        requestBody.setUserId("hiqh");
        updateCardOfflineAmountRequest.setRequestBody(requestBody);
        Assert.assertNotNull(cardLimitsClient.updateCardOfflineAmount(updateCardOfflineAmountRequest));
    }

    @Test
    public void updateCardLimitTest() throws Exception {
        PrepareTransitRequest prepareTransitRequest = new PrepareTransitRequest();
        Mockito.when(cardLimitsUtils.getEncryptedReqPayload(any(String.class), any(String.class))).thenReturn(prepareTransitRequest);
        String body = "{\n" +
                "    \"response\": {\n" +
                "      \"Header\": {\n" +
                "        \"AuthSessionInfo\": {\n" +
                "          \"AuthUserID\": \"135699\",\n" +
                "          \"AuthPassword\": \"z4PhNX7vuL3xVChQ1m2AB9Yg5AULVxXcg/SpIdNs6c5H0NE8XYXysP+DGNKHfuwvY7kxvUdBeoGlODJ6+SfaPg==\",\n" +
                "          \"UserCode\": {},\n" +
                "          \"Language\": \"EN\",\n" +
                "          \"MbrId\": \"1\"\n" +
                "        },\n" +
                "        \"subHeader\": {\n" +
                "          \"RequestUUID\": \"1\",\n" +
                "          \"ServiceRequestId\": \"?\",\n" +
                "          \"ServiceRequestVersion\": \"1.0\",\n" +
                "          \"ChannelId\": \"ESB\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"body\": {\n" +
                "        \"TopupToPrepaidResponse\": {\n" +
                "          \"encryptedResponse\": \"wHXMI+91SARsu/z9LmaQoGcKgPA9oWvtsNIhdbwB31uBcbgfYr0vVkhPD59qtgdIO2VSfx/96GhkCQ4Y9+XfqboWnrkIoFTAu2zQ42QL0+z0mHFWCWkreRSzimQYAGBtQLt2iDN7vUQWQyRQNx/TqXSdZUH/z4QBpggknF7V7S0YAYdixtmnCQ31wzmL5Bz05UtYz6NwQNRVLatRoqDcdSId3cgzSC2JAsZN6zQnc9pa/6IajUNMI8bSF/nnAlqhRMPZ/tb/EPK7dtoEY5XGoOK1UZZzMXEZFAq7maX06v4jbrC5jZi7IaEW95Um1TcvWmoWSXrM7j7+h2Z2OtFabldV3hG2H+bnNxyW1GfROTmOuv1/\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted().body(body);
        PowerMockito.when(HttpClientUtils.httpPostRequest(any(String.class),any(PrepareTransitRequest.class))).thenReturn(responseEntity);
        JsonNode actualObj = mapper.readTree(body);
        Mockito.when(cardLimitsUtils.addTypeResponse(any(ResponseEntity.class), any(String.class))).thenReturn(actualObj);
        UpdateCardLimitRequestRequest updateCardLimitRequestRequest = new UpdateCardLimitRequestRequest();
        com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody requestBody = new com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.RequestBody();
        requestBody.setCardNumber("12333");
        requestBody.setLanguage("EN");
        requestBody.setPassword("hhwgh");
        requestBody.setMbrId("1");
        requestBody.setUserId("hiqh");
        requestBody.setLimitType("L");
        requestBody.setDailyAmount("223");
        requestBody.setMaximumDailyAmount("123");
        requestBody.setMaximumMonthlyCount("");
        requestBody.setMaximumYearlyCount("");
        requestBody.setMaximumYearlyCount("");
        requestBody.setRestrictEcommerceTransaction("true");
        requestBody.setRestrictOfflineTransaction("true");
        requestBody.setRestrictEmvTransactionWithoutPin("true");
        requestBody.setRestrictOnlineContactlessTransaction("true");
        updateCardLimitRequestRequest.setRequestBody(requestBody);
        Assert.assertNotNull(cardLimitsClient.updateCardLimit(updateCardLimitRequestRequest));
    }
}
