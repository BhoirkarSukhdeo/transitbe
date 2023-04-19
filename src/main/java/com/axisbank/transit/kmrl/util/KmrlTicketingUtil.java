package com.axisbank.transit.kmrl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class KmrlTicketingUtil {

    @Value("${kmrl.ticket.username}")
    private String username;


    public String prepareSoapRequest(String requestBody, String token) {
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"><soap12:Header><TokenHeader xmlns=\"http://asis-services.com/\"><Username>{0}</Username><Token>{1}</Token></TokenHeader></soap12:Header><soap12:Body>{2}</soap12:Body></soap12:Envelope>";
        String formattedRequest = MessageFormat.format(request, username, token, requestBody);
        log.info("Kmrl Request: {}", formattedRequest);
        return formattedRequest;
    }
}
