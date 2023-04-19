package com.axisbank.transit.transitCardAPI.util;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CardLimitsUtils {

    @Value("${cardlimit.secret.key}")
    public  String secretKey;

    @Value("${update.limit.service.request_id}")
    private String serviceReqId;

    @Value("${update.limit.channel_id}")
    private String channelId;

    public PrepareTransitRequest getEncryptedReqPayload(String key, String plainTextReq) throws Exception {
        Map<String, EncryptedRequest> body = new HashMap<>();
        log.info("Plain Text to encrypt:{}",plainTextReq);
        String encryptedReqStr = EncryptionUtil.aesEncrypt(plainTextReq,secretKey);
        EncryptedRequest encryptedRequest = new EncryptedRequest();
        encryptedRequest.setEncryptedRequest(encryptedReqStr);
        Header header = getTransitHeader(getTransitSubHeader());
        body.put(key, encryptedRequest);
        return getTransitRequest(body,header);
    }

    public  Header getTransitHeader(SubHeader subHeader) {
        Header header = new Header();
        header.setSubHeader(subHeader);
        return header;
    }

    public  SubHeader getTransitSubHeader() {
        SubHeader subHeader = new SubHeader();
        subHeader.setServiceRequestId(serviceReqId);
        subHeader.setServiceRequestVersion(TransitCardAPIConstants.SERVICE_REQUEST_VERSION);
        subHeader.setChannelId(channelId);
        subHeader.setRequestUUID(CommonUtils.generateRandomString(20));
        return subHeader;
    }

    public  PrepareTransitRequest getTransitRequest(Map<String, ?> body , Header header) throws JsonProcessingException {
        Request request =  new Request();
        PrepareTransitRequest prepareTransitRequest =  new PrepareTransitRequest();
        request.setBody(body);
        request.setHeader(header);
        prepareTransitRequest.setRequest(request);
        log.info("Transit Json Request :{}", CommonUtils.convertObjectToJsonString(prepareTransitRequest));
        return prepareTransitRequest;
    }

    public JsonNode addTypeResponse(ResponseEntity<String> responseEntity, String responseType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());
        log.info("Response body:{}",responseEntity.getBody());
        JsonNode responseBody = jsonResponse.get("response").get("body");
        JsonNode encryptedResponse = responseBody.get(responseType) ==null? responseBody.get("Fault").get("encryptedResponse"):
                responseBody.get(responseType).get("encryptedResponse");
        log.info("Encrypted Response: {}",encryptedResponse.asText());
        String decryptedResponse = EncryptionUtil.aesDecrypt(encryptedResponse.asText(),secretKey);
        log.info("Decrypted String:{}",decryptedResponse);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(responseType,new JSONObject(decryptedResponse));
        log.info("Decrypted Response: {} ",jsonObject.toString());
        return objectMapper.readTree(jsonObject.toString());
    }
}
