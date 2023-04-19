package com.axisbank.transit.transitCardAPI.util;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EncryptionUtil.class, Base64.class})
public class TransitUtilsTests extends BaseTest {

    @InjectMocks
    @Autowired
    TransitUtils transitUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(EncryptionUtil.class);
        PowerMockito.mockStatic(Base64.class);
        ReflectionTestUtils.setField(transitUtils, "serviceReqId", "xyz");
        ReflectionTestUtils.setField(transitUtils, "authUserId", "xyz");
        ReflectionTestUtils.setField(transitUtils, "authPassword", "xyz");
        ReflectionTestUtils.setField(transitUtils, "serviceReqId", "xyz");
        ReflectionTestUtils.setField(transitUtils, "channelId", "xyz");
        ReflectionTestUtils.setField(transitUtils, "checksumKey", "xyz");
        ReflectionTestUtils.setField(transitUtils, "secretKey", "xyz");
    }

    @Test
    public void getEncryptedReqPayloadTest() throws Exception {
        PowerMockito.when(EncryptionUtil.aesEncrypt(any(String.class),any(String.class))).thenReturn("abc");
        Assert.assertNotNull(transitUtils.getEncryptedReqPayload("123", "pass123"));
    }

    @Test
    public void getChecksumReqPayloadTest() throws Exception {
        PowerMockito.when(Base64.encodeBase64String(any(byte[].class))).thenReturn("signature");
        Assert.assertNotNull(transitUtils.getChecksumReqPayload("abc", "abc"));
    }

    @Test
    public void addTypeResponseTest() throws Exception {
        String body = "{\n" +
                "    \"Response\": {\n" +
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
                "      \"Body\": {\n" +
                "        \"TopupToPrepaidResponse\": {\n" +
                "          \"encryptedResponse\": \"wHXMI+91SARsu/z9LmaQoGcKgPA9oWvtsNIhdbwB31uBcbgfYr0vVkhPD59qtgdIO2VSfx/96GhkCQ4Y9+XfqboWnrkIoFTAu2zQ42QL0+z0mHFWCWkreRSzimQYAGBtQLt2iDN7vUQWQyRQNx/TqXSdZUH/z4QBpggknF7V7S0YAYdixtmnCQ31wzmL5Bz05UtYz6NwQNRVLatRoqDcdSId3cgzSC2JAsZN6zQnc9pa/6IajUNMI8bSF/nnAlqhRMPZ/tb/EPK7dtoEY5XGoOK1UZZzMXEZFAq7maX06v4jbrC5jZi7IaEW95Um1TcvWmoWSXrM7j7+h2Z2OtFabldV3hG2H+bnNxyW1GfROTmOuv1/\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted().body(body);

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

        PowerMockito.when(EncryptionUtil.aesDecrypt(any(String.class),any(String.class))).thenReturn(getAllTxnJson);

        Assert.assertNotNull(transitUtils.addTypeResponse(responseEntity, "TopupToPrepaidResponse"));

    }

    @Test
    public void addTypeResponseChecksumTest() throws Exception {
        String body = "{\n" +
                "    \"Response\": {\n" +
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
                "      \"Body\": {\n" +
                "        \"TopupToPrepaidResponse\": {\n" +
                "          \"encryptedResponse\": \"wHXMI+91SARsu/z9LmaQoGcKgPA9oWvtsNIhdbwB31uBcbgfYr0vVkhPD59qtgdIO2VSfx/96GhkCQ4Y9+XfqboWnrkIoFTAu2zQ42QL0+z0mHFWCWkreRSzimQYAGBtQLt2iDN7vUQWQyRQNx/TqXSdZUH/z4QBpggknF7V7S0YAYdixtmnCQ31wzmL5Bz05UtYz6NwQNRVLatRoqDcdSId3cgzSC2JAsZN6zQnc9pa/6IajUNMI8bSF/nnAlqhRMPZ/tb/EPK7dtoEY5XGoOK1UZZzMXEZFAq7maX06v4jbrC5jZi7IaEW95Um1TcvWmoWSXrM7j7+h2Z2OtFabldV3hG2H+bnNxyW1GfROTmOuv1/\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted().body(body);
        Assert.assertNotNull(transitUtils.addTypeResponseChecksum(responseEntity));
    }
}
