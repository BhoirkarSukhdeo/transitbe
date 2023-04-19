package com.axisbank.transit.core.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendSmsUtil {
    @Autowired
    AxiomSmsClient axiomSmsClient;

    @Autowired
    TwilioClientUtils twilioClientUtils;

    @Value("${app.sms.provider:twillio}")
    private String smsProvider;

    public void sendSms(String message, String mobileNumber) {
        switch (smsProvider) {
            case "axiom":
                try {
                    axiomSmsClient.sendSMS(message, mobileNumber);
                } catch (Exception ex) {
                    log.error("Failed to send Axiom SMS: {}", ex.getMessage());
                }
                break;
            default:
                try {
                    twilioClientUtils.sendSMS(message, mobileNumber);
                } catch (Exception ex) {
                    log.error("Failed to send Twillio SMS: {}", ex.getMessage());
                }
                break;
        }
    }
}
