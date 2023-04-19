package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import static com.axisbank.transit.core.shared.constants.UtilsConstants.AXIOM_SMS_REQUEST;

@Slf4j
@Component
public class AxiomSmsClient {

    @Value("${axiom.userid}")
    private String userid;

    @Value("${axiom.pwd}")
    private String pwd;

    @Value("${axiom.dcode}")
    private String dcode;

    @Value("${axiom.baseUrl}")
    private String baseURL;

    /**
     * send sms using axioms sms alert of axis bank server
     * @param sms
     * @param mobile (without country code e.g 94**********)
     * @throws Exception
     */
    public void sendSMS(String sms, String mobile) throws Exception {
        log.info("Request receive for sending OTP to given mobile");
        if(mobile.length()<10 || mobile.length()>14) {
            throw new Exception("Invalid Mobile Number");
        }
        mobile = mobile;
        String requestBody = MessageFormat.format(AXIOM_SMS_REQUEST, userid, pwd, mobile, sms, dcode);
        log.debug("RequestBody:{}",requestBody);
        String soapResponse = SoapUtils.getSoapResponse(baseURL, requestBody, "POST");
        log.info("Soap Resp:{}",soapResponse);
        if (!soapResponse.startsWith("APP")) {
            throw new Exception("Failed to send OTP");
        }
    }
}
