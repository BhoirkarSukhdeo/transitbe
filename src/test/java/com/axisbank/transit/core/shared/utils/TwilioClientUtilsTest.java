package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import com.twilio.Twilio;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.Mockito.doNothing;

public class TwilioClientUtilsTest extends BaseTest {


    @Value("${twilio.acc.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.mobile.no}")
    private String twilioMobileNo;

    @Autowired
    private TwilioClientUtils twilioClientUtils;

    @Autowired
    private OtpUtils otpUtils;

    @Before
    public void setUp() {
        accountSid="123abc";
        authToken="abc123";
        Twilio.init(accountSid,authToken);
        System.out.print("Hello Config test:{}");
    }

    @Test
    public void sendSMSTest() throws Exception {
        String mobile ="9535158983";
        String smsContent = "Hello five transit";
        TwilioClientUtils twilioClientUtils = Mockito.mock(TwilioClientUtils.class);
        doNothing().when(twilioClientUtils).sendSMS(smsContent, mobile);

    }
}
