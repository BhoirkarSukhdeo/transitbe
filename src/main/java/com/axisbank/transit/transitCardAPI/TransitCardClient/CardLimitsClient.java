package com.axisbank.transit.transitCardAPI.TransitCardClient;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardLimit.UpdateCardLimitRequestRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCardOfflineAmount.UpdateCardOfflineAmountRequest;
import com.axisbank.transit.transitCardAPI.util.CardLimitsUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardLimitsClient {

    @Autowired
    private CardLimitsUtils cardLimitsUtils;

    @Value("${update.cardlimit.url}")
    private String updateCardLimitUrl;

    @Value("${update.cardOfflineAmount.url}")
    private String updateCardOfflineAmountUrl;

    public JsonNode updateCardOfflineAmount(UpdateCardOfflineAmountRequest updateCardOfflineAmountRequest) throws Exception {
        log.info("Request Received in updateCardOfflineAmount");
        String plainTxtReq = CommonUtils.convertObjectToJsonString(updateCardOfflineAmountRequest);
        PrepareTransitRequest transitRequest =cardLimitsUtils.getEncryptedReqPayload(TransitCardAPIConstants.UPDATE_CARD_OFFLINE_AMOUNT_REQUEST, plainTxtReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(updateCardOfflineAmountUrl,transitRequest);
        return cardLimitsUtils.addTypeResponse(responseEntity, "updateCardOfflineAmountResponse");
    }

    public JsonNode updateCardLimit(UpdateCardLimitRequestRequest updateCardLimitRequestRequest) throws Exception {
        log.info("Request Received in updateCardLimit");
        String plainTxtReq = CommonUtils.convertObjectToJsonString(updateCardLimitRequestRequest);
        PrepareTransitRequest transitRequest =cardLimitsUtils.getEncryptedReqPayload(TransitCardAPIConstants.UPDATE_CARD_LIMIT_REQUEST, plainTxtReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(updateCardLimitUrl,transitRequest);
        return cardLimitsUtils.addTypeResponse(responseEntity, "updateCardLimitResponse");
    }
}
