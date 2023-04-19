package com.axisbank.transit.transitCardAPI.TransitCardClient;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.model.request.CustExistCheck.CustExistCheckRequest;
import com.axisbank.transit.transitCardAPI.model.request.availableLimit.AvailableLimitRequest;
import com.axisbank.transit.transitCardAPI.model.request.ppim.PpimHeader;
import com.axisbank.transit.transitCardAPI.model.request.ppim.PpimRequest;
import com.axisbank.transit.transitCardAPI.model.request.ppim.PreparePPIMRequest;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.util.PpimsUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.AVAILABLE_LIMIT_REQUEST;
import static com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants.CUST_EXIST_CHECK_REQUEST;

@Component
@Slf4j
public class PPIMClient {

    @Autowired
    private PpimsUtils ppimsUtils;

    @Value("${transit.card.ppim.host}")
    private  String transitPPIMDomain;

    public JsonNode getReqForResForCustExistStatus(CustExistCheckRequest custExistCheckRequest) throws Exception {
        log.info("Request receive to get details from PPIM CustExistStatus API");
        PreparePPIMRequest preparePPIMRequest =getPpimFinalRequest("CustExistStatusRequest", custExistCheckRequest);
        String plainTxtReq = CommonUtils.convertObjectToJsonString(preparePPIMRequest);
        PrepareTransitRequest transitRequest = ppimsUtils.getEncryptedReqPayload(CUST_EXIST_CHECK_REQUEST,plainTxtReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitPPIMDomain,transitRequest);
        return ppimsUtils.addTypeResponse(responseEntity, "custExistCheckResponse");
    }

    public JsonNode getReqAndResForAvailableLimit(AvailableLimitRequest availableLimitRequest) throws Exception {
        log.info("Request receive to get details from PPIM availableLimit API");
        PreparePPIMRequest preparePPIMRequest =getPpimFinalRequest("AvailableLimitRequest", availableLimitRequest);
        String plainTxtReq = CommonUtils.convertObjectToJsonString(preparePPIMRequest);
        PrepareTransitRequest transitRequest = ppimsUtils.getEncryptedReqPayload(AVAILABLE_LIMIT_REQUEST,plainTxtReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitPPIMDomain,transitRequest);
        return ppimsUtils.addTypeResponse(responseEntity, "availableLimtResponse");
    }

    public PreparePPIMRequest getPpimFinalRequest(String key, Object object) throws Exception {
        PreparePPIMRequest preparePPIMRequest = new PreparePPIMRequest();
        PpimRequest ppimRequest = new PpimRequest();
        Map<String, Object> body = new HashedMap<>();
        PpimHeader ppimHeader = ppimsUtils.getPPIMHeader();
        if(object instanceof CustExistCheckRequest)
            body.put(key,object);
        else if(object instanceof AvailableLimitRequest)
            body.put(key, object);
        else
            throw new Exception("Invalid PPIM API request");

        ppimRequest.setBody(body);
        ppimRequest.setHeader(ppimHeader);
        preparePPIMRequest.setRequest(ppimRequest);
        log.debug("Printing complete Request: {} ", CommonUtils.convertObjectToJsonString(preparePPIMRequest));
        return preparePPIMRequest;
    }

}

