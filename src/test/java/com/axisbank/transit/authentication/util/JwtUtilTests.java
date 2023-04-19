package com.axisbank.transit.authentication.util;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.core.shared.utils.RedisClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class JwtUtilTests extends BaseTest {

    private User userDetails;
    private String token;

    @InjectMocks
    @Autowired
    JwtUtil jwtUtil;

    @Mock
    RedisClient redisClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDetails = new User("3344227733", "", new ArrayList<>());
        jwtUtil.setSecret("123456");
        jwtUtil.setJwtExpirationInSec(3000);
        jwtUtil.setRefreshExpirationDateInSec(6000);
        jwtUtil.setIdleSessionTimeOut(600);
        token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5MTg4OTk4OTk3MDkiLCJpc1JlZnJlc2hUb2tlbiI6dHJ1ZSwiZXhwIjoxNjEwMTU5ODk0LCJpYXQiOjE2MDk3NDk4Mjh9.mNuDOe3ZIdr6p7Aknx58MeCoqLfKqGJd_3Y9JVdFZvBsoILY2Mo1kaP3x9IoXpQ3d5SrQ4qIZN4FPv5E0zM7zA";
    }

    @Test
    public void generateTokenTest() {
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        String token = jwtUtil.generateToken(userDetails);
        Assert.assertNotNull(token);
    }

    @Test
    public void doGenerateRefreshTokenTest() {
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("isRefreshToken", true);
        String token = jwtUtil.doGenerateRefreshToken(refreshClaims, "3344227733");
        Assert.assertNotNull(token);
    }

    @Test
    public void validateTokenTest() {
        User userDetails = new User("2233445566", "", new ArrayList<>());
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        doNothing().when(redisClient).deletePattern(any(String.class));
        String accessToken = jwtUtil.generateToken(userDetails);
        when(redisClient.getValue(any(String.class))).thenReturn("xyz");
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        boolean result = jwtUtil.validateToken(accessToken);
        Assert.assertEquals(true, result);
    }

    @Test
    public void getFullAutherizationTokenTest() {
        doNothing().when(redisClient).setValue(any(String.class), any(String.class), any(Long.class));
        String token = jwtUtil.getFullAutherizationToken("3344227733");
        Assert.assertNotNull(token);
    }
}
