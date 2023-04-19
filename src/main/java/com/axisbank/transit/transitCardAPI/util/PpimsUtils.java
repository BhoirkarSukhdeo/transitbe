package com.axisbank.transit.transitCardAPI.util;


import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.ppim.PpimHeader;
import com.axisbank.transit.transitCardAPI.model.request.ppim.PpimSessionInfo;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.Header;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.Request;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.SubHeader;
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
public class PpimsUtils {

    @Value("${ppim.service.request_id}")
    private String serviceReqId;

    @Value("${ppim.channel_id}")
    private String channelId;

    @Value("${ppim.channel_idV2}")
    private String channelIdV2;

    @Value("${ppim.username}")
    private String ppimUserName;

    @Value("${ppim.password}")
    private String ppimPassword;

    @Value("${ppim.aes.key}")
    private String ppimAesKey;

    @Value("${ppim.iv}")
    private String ppimIV;



    public  SubHeader getTransitSubHeader() {
        SubHeader subHeader = new SubHeader();
        subHeader.setServiceRequestId(serviceReqId);
        subHeader.setServiceRequestVersion(TransitCardAPIConstants.SERVICE_REQUEST_VERSION);
        subHeader.setChannelId(channelId);
        subHeader.setRequestUUID(CommonUtils.generateRandomString(20));
        return subHeader;
    }

    public Header getTransitHeader(SubHeader subHeader) {
        Header header = new Header();
        header.setSubHeader(subHeader);
        return header;
    }

    /***
     * This method will be responsible to prepare Transit Card API's request payload
     * and return PrepareTransitRequest object
     *
     * @param key
     * @param plainTextReq
     * @return
     * @throws Exception
     */
    public  PrepareTransitRequest getEncryptedReqPayload(String key, String plainTextReq) throws Exception {
        Map<String, String> req = new HashMap<>();
        Map<String, Map<String, String>> body = new HashMap<>();
        log.info("Plain Text to encrypt:{}",plainTextReq);
        req.put("req", EncryptionUtil.aesEncryptPpim(plainTextReq,ppimAesKey,ppimIV));
        Header header = getTransitHeader(getTransitSubHeader());
        body.put(key, req);
        return getTransitRequest(body, header);
    }

    public PpimHeader getPPIMHeader() {
        PpimHeader ppimHeader = new PpimHeader();
        ppimHeader.setSessionInfo(new PpimSessionInfo(ppimUserName, ppimPassword,channelIdV2));
        return ppimHeader;
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
        JsonNode responseBody = jsonResponse.get("Response").get("Body");
        JsonNode encryptedResponse = responseBody.get(responseType) ==null? responseBody.get("Fault").get("res"):
                responseBody.get(responseType).get("res");
        log.info("Encrypted Response: {}",encryptedResponse.asText());
        String decryptedResponse = EncryptionUtil.aesDecryptPpim(encryptedResponse.asText(),ppimAesKey, ppimIV);
        log.info("Decrypted String:{}",decryptedResponse);
        return objectMapper.readTree(decryptedResponse).get("Response").get("Body");
    }


}
