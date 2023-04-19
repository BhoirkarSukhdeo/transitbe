package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SoapUtils.class)
public class AxiomSmsClientTest extends BaseTest {

    @InjectMocks
    @Autowired
    AxiomSmsClient axiomSmsClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendSMSTest() throws Exception {
        PowerMockito.mockStatic(SoapUtils.class);
        String reqBody = "<push><userid>null</userid><pwd>null</pwd><ctype>1</ctype><sender>AxisBk</sender><pno>2233556677</pno><msgtxt>Hello</msgtxt><dcode>null</dcode><alert>0</alert><msgtype>S</msgtype><priority>2</priority></push>";
        PowerMockito.when(SoapUtils.getSoapResponse(null, reqBody, "POST")).thenReturn("xyz");
        try {
            axiomSmsClient.sendSMS("Hello", "2233556677");
        } catch (Exception exception) {
            Assert.assertEquals("Failed to send OTP", exception.getMessage());
        }
    }
}
