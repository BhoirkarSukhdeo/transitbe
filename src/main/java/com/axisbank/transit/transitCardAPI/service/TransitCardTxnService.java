package com.axisbank.transit.transitCardAPI.service;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.transitCardAPI.exceptions.BlockedCardException;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.BlockCardDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardTransactionDTO;
import com.axisbank.transit.transitCardAPI.model.request.BlockCardRequest;
import com.axisbank.transit.transitCardAPI.model.request.LinkCardRequest;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.UpdateCardStatus;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.fasterxml.jackson.databind.JsonNode;
import in.juspay.model.Order;
import org.json.JSONObject;

import java.util.List;

public interface TransitCardTxnService {
     TopupToPrepaid getTopupToPrepaidRequest(String cardNumber, TopupRequest request);
     TopupToPrepaid getDebitFromTransitRequest(String cardNumber, TopupRequest request);
     GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalanceInfoRequest(CardDetailsDAO cardDetailsDAO) throws Exception;
     UpdateCardStatus getBlockCardRequest(CardDetailsDAO cardDetailsDAO) throws Exception;
     CardVerificationRespRequest getCardVerificationRequest(String mobileNo, String lastFourDigitCardNo);
     AuthenticationDAO linkCardService(LinkCardRequest linkCardRequest) throws Exception;
     List<TransitCardTransactionDTO> getTransitCardTransactions(int pageNo, int pageSize, String txnType) throws Exception;
     BlockCardDTO blockOrUnblockTransitCard(BlockCardRequest blockCardRequest) throws Exception;
     TransitCardInfoDTO getTransitCardInfo() throws Exception;
     Order createTopupRequest(AuthenticationDAO authenticationDAO, TopupRequest request) throws Exception;
     String getTransitCardNo(String cardToken) throws Exception;
     void createTransitCardRefund(String cardToken, String txnId) throws Exception;
     String getBlockStatus(String status, String subStatus);
     boolean verifyCardBin(String cardNumber);
     boolean isRegistrationAllowed(String status);
     String getUniqueCustIdFromPPIM(String mobileNumber, String cardNumber) throws Exception;
     Double getAvailableLimitFromPPIM(String uniqueCustomerId) throws Exception;
     JSONObject fetchCustomerCardInfo(String mobile, String dob, String name, String lastName) throws Exception;
    JsonNode getCardAllTransactions(String take, String skip, String startDate, String endDate, String orderBy) throws Exception;
    void verifyBlockOperation(String cardNumber) throws BlockedCardException, Exception;
     AuthenticationDAO linkReplacementCard(AuthenticationDAO authenticationDAO);
     TransitCardInfoDTO getTransitCardInfo(String cardNumber) throws Exception;
}
