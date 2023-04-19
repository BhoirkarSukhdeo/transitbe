package com.axisbank.transit.transitCardAPI.util;


import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.EncryptionUtil;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class TransitUtils {
    ObjectMapper mapper = new ObjectMapper();
    @Value("${transit.secret_key}")
    public  String secretKey;

    @Value("${transit.auth.user_id}")
    private String authUserId;

    @Value("${transit.auth.password}")
    private String authPassword;

    @Value("${transit.service.request_id}")
    private String serviceReqId;

    @Value("${transit.channel_id}")
    private String channelId;

    @Value("${transit.checksum.key}")
    private String checksumKey;



    public  AuthSessionInfo getTransitAuthSessionInfo(String authUserId, String authPass) {
        AuthSessionInfo authSessionInfo = new AuthSessionInfo();
        authSessionInfo.setMbrId(TransitCardAPIConstants.MBRID);
        authSessionInfo.setLanguage(TransitCardAPIConstants.LANGUAGE);
        authSessionInfo.setAuthPassword(authPass);
        authSessionInfo.setAuthUserID(authUserId);
        authSessionInfo.setUserCode(authUserId);
        return authSessionInfo;
    }

    public  SubHeader getTransitSubHeader() {
        SubHeader subHeader = new SubHeader();
        subHeader.setServiceRequestId(serviceReqId);
        subHeader.setServiceRequestVersion(TransitCardAPIConstants.SERVICE_REQUEST_VERSION);
        subHeader.setChannelId(channelId);
        subHeader.setRequestUUID(CommonUtils.generateRandomString(20));
        return subHeader;
    }
    public  SubHeader getTransitSubHeaderChecksum(String checksum) {
        SubHeader subHeader = new SubHeader();
        subHeader.setServiceRequestId(serviceReqId);
        subHeader.setServiceRequestVersion(TransitCardAPIConstants.SERVICE_REQUEST_VERSION);
        subHeader.setChannelId(channelId);
        subHeader.setChecksum(checksum);
        subHeader.setRequestUUID(CommonUtils.generateRandomString(20));
        return subHeader;
    }

    public  Header getTransitHeader(AuthSessionInfo authSessionInfo, SubHeader subHeader) {
        Header header = new Header();
        header.setSubHeader(subHeader);
        header.setAuthSessionInfo(authSessionInfo);
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
        Map<String, EncryptedRequest> body = new HashMap<>();
        log.info("Plain Text to encrypt:{}",plainTextReq);
        String encryptedReqStr = EncryptionUtil.aesEncrypt(plainTextReq,secretKey);
        EncryptedRequest encryptedRequest = new EncryptedRequest();
        encryptedRequest.setEncryptedRequest(encryptedReqStr);
        AuthSessionInfo authSessionInfo = getTransitAuthSessionInfo(authUserId, authPassword);
        Header header = getTransitHeader(authSessionInfo,getTransitSubHeader());
        body.put(key, encryptedRequest);
        return getTransitRequest(body,header);
    }

    public PrepareTransitRequest getChecksumReqPayload(String key, Object plainTextReq) throws IOException {
        String stringData = getChecksumString(plainTextReq,"");
        log.info("CheckSum String: {}", stringData);
        String checksum = createSignature(stringData);
        Map<String, Object> body = new HashMap<>();
        AuthSessionInfo authSessionInfo = getTransitAuthSessionInfo(authUserId, authPassword);
        Header header = getTransitHeader(authSessionInfo,getTransitSubHeaderChecksum(checksum));
        body.put(key, plainTextReq);
        return getTransitRequest(body,header);
    }

    private String getChecksumString(Object plainTextReq, String checkSumStringData){
        try{
        JsonNode node = mapper.valueToTree(plainTextReq);
            Iterator<String> itr = node.fieldNames();
            String key_field="";
            while (itr.hasNext()) {  //to get the key fields
                key_field = itr.next();
                if (node.get(key_field).isObject()){
                    return getChecksumString(node.get(key_field),checkSumStringData);
                }
                checkSumStringData+=node.get(key_field).asText("");
            }
            return checkSumStringData;
        }
        catch (Exception ex ){
            log.error("Failed to generate CheckSum :{}",ex.getMessage());
            throw ex;
        }
    }

    public String createSignature(String message) {
        String signature="";
        try{
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            signature = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            signature= signature.replaceAll("\r", "").replaceAll("\n", "");
        }
        catch(Exception e)
        {
            signature="ERROR";
        }
        return signature;
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

    public JsonNode addTypeResponseChecksum(ResponseEntity<String> responseEntity) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());
        log.info("Response body:{}",responseEntity.getBody());
        JsonNode response = jsonResponse.get("Response").get("Body");
        log.info("CheckSum Response: {} ",response.asText());
        return response;
    }

}
