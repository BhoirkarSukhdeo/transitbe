package com.axisbank.transit.transitCardAPI.TransitCardClient;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.getCustomerDtlsRequest.GetCustomerDtlsRequest;
import com.axisbank.transit.transitCardAPI.model.request.getEntityDoc.GetEntityDocRequest;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.util.FinacleUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinacleClient {

    @Autowired
    private FinacleUtils finacleUtils;

    @Value("${transit.card.finacle.host}")
    private  String transitFinacleDomain;

    @Value("${transit.finsp.host}")
    private String transitFinSPDomain;

    public JsonNode getCustomerDetails(GetCustomerDtlsRequest getCustomerDtlsRequest) throws Exception {
        log.info("Request receive for fetching CustomerDetail from Finacle domain");
        PrepareTransitRequest transitRequest = finacleUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CUSTOMER_DETAILS_URI,
                getCustomerDtlsRequest);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitFinacleDomain,transitRequest);
        return finacleUtils.addTypeResponseChecksum(responseEntity);
    }

    public JsonNode getEntityDoc(GetEntityDocRequest getEntityDocRequest) throws Exception {
        log.info("Request Receive to getEntityDoc.");
        String plainTxtReq = CommonUtils.convertObjectToJsonString(getEntityDocRequest);
        PrepareTransitRequest transitRequest =finacleUtils.getEncryptedReqPayload(TransitCardAPIConstants.GET_ENTITY_DOC_URI, plainTxtReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitFinSPDomain,transitRequest);
        return finacleUtils.addTypeResponse(responseEntity, "getEntityDocResponse");
    }
}
