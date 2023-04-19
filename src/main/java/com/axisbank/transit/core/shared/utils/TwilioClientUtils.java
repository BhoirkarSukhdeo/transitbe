package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.core.shared.constants.UtilsConstants;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TwilioClientUtils {

    @Value("${twilio.acc.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.mobile.no}")
    private String twilioMobileNo;


    /**
     * Send OTP to given mobile with given otp value.
     *
     * @param sms
     * @Param mobile
     * @return
     * @throws Exception
     */
    public void sendSMS(String sms, String mobile) throws Exception {
        log.info("Request receive for sending OTP to given mobile");
        Twilio.init(accountSid,authToken);
        if(mobile.length()<10 || mobile.length()>14) {
            throw new Exception("Invalid Mobile Number");
        }
        Message message = null;
            mobile = UtilsConstants.COUNTRY_CODE+mobile;
        try {
            message = Message.creator(new com.twilio.type.PhoneNumber(mobile),
                    new com.twilio.type.PhoneNumber(twilioMobileNo),sms).create();
        } catch (Exception e) {
            log.info("Failed to send OTP: {}", e.toString());
            throw new Exception("Failed to send OTP");
        }
        log.info("Message status: {}",message.getStatus());
    }
}
