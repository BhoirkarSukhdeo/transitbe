package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
public class OtpUtils {
    @Value("${app.otp.length}")
    private Integer length;
    @Value("${app.otp.expiration}")
    private int expiration;
    @Value("${app.otp.istest}")
    private boolean isTestOtp;
    @Autowired
    private RedisClient redisClient;

    /**
     * Generates OTP for given user and store it in redis cache for specified expiration time in seconds.
     * @param username
     * @return
     * @throws Exception
     */
    public String generateOtp(String username) throws Exception{
        long ttl = 0;
        try{
            ttl = redisClient.getTtl(username);
        } catch (Exception ex) {
            log.error("Exception while getting ttl:{}", ex.getMessage());
        }
        if (ttl > 0) {
            throw new Exception("OTP already generated, retry in "+ ttl +" secs");
        }
        String otp = generateRandInt(length);
        if(isTestOtp)
            otp = "123456";
        redisClient.setValue(username, otp, expiration);
        return otp;
    }

    /**
     * Verifies OTP of given userame and return boolean value.
     * @param username
     * @param otp
     * @return
     * @throws Exception
     */
    public boolean verifyOtp(String username, String otp) throws Exception {
        try {
            log.info("Verifying User: {}, OTP:{}",CommonUtils.maskString(username,0,username.length()-4,'*'),
                    CommonUtils.maskString(otp,0,otp.length()-2,'*'));
            String userOtp = redisClient.getValue(username);
            return userOtp.equals(otp);
        } catch (Exception e) {
            log.error("Exception in verifyOtp: {}", e.getMessage());
            throw  new  Exception("OTP not generated/expired for given user");
        }
    }

    /**
     * Once OTP is verified and key should be removed from redis cache to avoid illegal use of the OTP.
     * @param username
     */
    public void  removeOtp(String username){
        redisClient.deleteKey(username);
    }

    /**
     * Generates Random Integer of given length
     * @param length
     * @return
     */
    private static String generateRandInt(int length)
    {
        String numbers = "0123456789";
        SecureRandom randomMethod = new SecureRandom();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++)
        {
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }
}
