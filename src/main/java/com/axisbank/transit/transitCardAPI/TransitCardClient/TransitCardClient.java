package com.axisbank.transit.transitCardAPI.TransitCardClient;


import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.HttpClientUtils;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.request.*;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllTransactions.GetCardAllTransactions;
import com.axisbank.transit.transitCardAPI.model.request.getCardInfoForPortal.GetCardInfoForPortal;
import com.axisbank.transit.transitCardAPI.model.request.getTxnByIdRequest.GetTxnByIdRequest;
import com.axisbank.transit.transitCardAPI.model.request.matchCardAndNewCustomer.MatchCardAndNewCustomer;
import com.axisbank.transit.transitCardAPI.model.request.topupToCardWithLastFourDigits.TopupToCardWithLastFourDigits;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal.TopupToPrepaidReversal;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid_Reversal.TopupToPrepaid_Reversal;
import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.PrepareTransitRequest;
import com.axisbank.transit.transitCardAPI.model.request.updateCustomer.UpdateCustomer;
import com.axisbank.transit.transitCardAPI.util.TransitUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransitCardClient {

    @Value("${transit.card.host}")
    private  String transitDomain;

    @Value("${transit.card.hostV2}")
    private  String transitDomainV2;

    @Autowired
    private TransitUtils transitUtils;

    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx V1 APIs xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public  JsonNode cardVerificationResp(CardVerificationRespRequest cardVerificationResp) throws Exception {
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.CARD_VERIFICATION_KEY,
                cardVerificationResp.getCardVerificationResp());
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain,transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode getCardAllLimitAndBalanceInfo(GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalanceInfoRequest) throws Exception {
        log.info("Request receive for fetching CardAllLimitANdBalanceInfo.");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CARD_ALL_LIMIT_AND_BALANCE_KEY,
                getCardAllLimitAndBalanceInfoRequest.getGetCardAllLimitAndBalanceInfo());
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode getCardAllTransactions(GetCardAllTransactions cardAllTransactionsReq) throws Exception {
        log.info("Request receive for  fetching CardAllTransactions");
        PrepareTransitRequest transitRequest  = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CARD_ALL_TXN_KEY,
                cardAllTransactionsReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain,transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode swGetCardInfo(SWGetCardInfoResponse swGetCardInfoResponseReq) throws Exception {
        log.info("Request Receive for fetching SWCard info");
        PrepareTransitRequest transitRequest  = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.SW_CARD_INFO_KEY,
                swGetCardInfoResponseReq);
        ResponseEntity<String> responseEntity =HttpClientUtils.httpPostRequest(transitDomain,transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }


    public  JsonNode getCityList(GetCityList getCityListReq) throws Exception {
        log.info("Request Receive for fetching the city List");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CITY_LIST_KEY,
                getCityListReq);
        ResponseEntity<String> responseEntity = HttpClientUtils.httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode getCustomerCardInfo(GetCustomerCardInfo getCustomerCardInfo) throws Exception {
        log.info("Request receive for fetching Customer Card info");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CUSTOMER_CARD_INFO_KEY,
                getCustomerCardInfo);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode getCustomer(GetCustomer getCustomerReq) throws Exception {
        log.info("Request receive for fetching customer details");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.GET_CUSTOMER_KEY,getCustomerReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode matchCardAndNewCustomer(MatchCardAndNewCustomer matchCardAndNewCustomerReq) throws Exception {
        log.info("Request receive for matching new Customer with Card.");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.MATCH_CARD_AND_NEW_CUSTOMER_KEY,
                matchCardAndNewCustomerReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, matchCardAndNewCustomerReq);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode topupCardWithLastFourDigits(TopupToCardWithLastFourDigits topupToCardWithLastFourDigitsReq) throws Exception {
        log.info("Request receive for Top-up Card with last four digits");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.TOPUP_TO_CARD_WITH_LAST_FOUR_DIGITS_KEY,
                topupToCardWithLastFourDigitsReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode updateCardStatus(UpdateCardStatus updateCardStatusReq) throws Exception {
        log.info("Request Receive for updating Card status");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.UPDATE_CARD_STATUS_KEY,
                updateCardStatusReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }

    public  JsonNode updateCustomer(UpdateCustomer updateCustomer) throws Exception {
        log.info("Request Receive for updateCustomer");
        PrepareTransitRequest transitRequest = transitUtils.getChecksumReqPayload(TransitCardAPIConstants.UPDATE_CUSTOMER_KEY,updateCustomer);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomain, transitRequest);
        return transitUtils.addTypeResponseChecksum(responseEntity);
    }
    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx V2 APIs xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    public  JsonNode fetchCardInfoForPortal(GetCardInfoForPortal getCardInfoForPortalReq) throws Exception {
        log.info("Request Receive for fetching Card Info for portal");
        String plainTextReq = CommonUtils.convertObjectToJsonString(getCardInfoForPortalReq);
        PrepareTransitRequest transitRequest = transitUtils.getEncryptedReqPayload(TransitCardAPIConstants.GET_CARD_INFO_FOR_PORTAL_KEY,plainTextReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomainV2, transitRequest);
        return transitUtils.addTypeResponse(responseEntity,"GetCardInfoForPortalResponse");
    }

    public  JsonNode getCardInfoForPortal(GetCardInfoForPortal getCardInfoForPortalReq) throws Exception {
        log.info("Request receive for fetching Card info for portal.");
        String plainTextReq = CommonUtils.convertObjectToJsonString(getCardInfoForPortalReq);
        PrepareTransitRequest transitRequest = transitUtils.getEncryptedReqPayload(TransitCardAPIConstants.GET_CARD_INFO_FOR_PORTAL_KEY,plainTextReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomainV2, transitRequest);
        return transitUtils.addTypeResponse(responseEntity,"GetCardInfoForPortalResponse");
    }

    public  JsonNode topupToPrepaidResponse(TopupToPrepaid topupToPrepaidReq) throws Exception {
        log.info("Request receive for topupToPrepaid.");
        String plainTextReq = CommonUtils.convertObjectToJsonString(topupToPrepaidReq);
        PrepareTransitRequest transitRequest = transitUtils.getEncryptedReqPayload(TransitCardAPIConstants.TOPUP_TO_PREPAID_KEY,plainTextReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomainV2, transitRequest);
        return transitUtils.addTypeResponse(responseEntity,"TopupToPrepaidResponse");
    }

    public  JsonNode topupToPrepaid_ReversalResponse(TopupToPrepaidReversal topupToPrepaid_reversalReq) throws Exception {
        log.info("Request receive for TopupToPrepaid_Reversal");
        String plainTextReq = CommonUtils.convertObjectToJsonString(topupToPrepaid_reversalReq);
        PrepareTransitRequest transitRequest = transitUtils.getEncryptedReqPayload(TransitCardAPIConstants.TOPUP_TO_PREPAID_REVERSAL_KEY,plainTextReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomainV2, transitRequest);
        return transitUtils.addTypeResponse(responseEntity,"TopupToPrepaid_ReversalResponse");
    }

    public  JsonNode getTxnByIdResponse(GetTxnByIdRequest getTxnByIdRequest) throws Exception {
        log.info("Request Receive for getTxnById");
        String plainTextReq = CommonUtils.convertObjectToJsonString(getTxnByIdRequest);
        PrepareTransitRequest transitRequest = transitUtils.getEncryptedReqPayload(TransitCardAPIConstants.GET_TXN_BY_ID_KEY,plainTextReq);
        ResponseEntity<String> responseEntity = HttpClientUtils
                .httpPostRequest(transitDomainV2, transitRequest);
        return transitUtils.addTypeResponse(responseEntity,"getTxnByIdResponse");
    }

}
