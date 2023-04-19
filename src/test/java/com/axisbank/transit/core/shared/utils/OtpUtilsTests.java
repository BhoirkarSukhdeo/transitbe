package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class OtpUtilsTests extends BaseTest {

    @Mock
    RedisClient redisClient;

    @InjectMocks
    @Autowired
    OtpUtils otpUtils;

    @Before
    public void setUp() throws Exception {
        // Initialize mocks created above
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void generateOtpTest() throws Exception {
        String username = "123456789";
        doReturn(0L).when(redisClient).getTtl(any(String.class));
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        String otp = otpUtils.generateOtp(username);
        Assert.assertNotNull(otp);
    }
}
