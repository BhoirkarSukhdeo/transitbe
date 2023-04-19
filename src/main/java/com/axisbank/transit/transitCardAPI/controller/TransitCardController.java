package com.axisbank.transit.transitCardAPI.controller;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.transitCardAPI.model.DAO.CardDetailsDAO;
import com.axisbank.transit.transitCardAPI.model.DTO.BlockCardDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardInfoDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.TransitCardTransactionDTO;
import com.axisbank.transit.transitCardAPI.model.request.BlockCardRequest;
import com.axisbank.transit.transitCardAPI.model.request.LinkCardRequest;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.cardVerificationRespRequest.CardVerificationRespRequest;
import com.axisbank.transit.transitCardAPI.model.request.getCardAllLimitAndBalanceInfoRequest.GetCardAllLimitAndBalanceInfoRequest;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.axisbank.transit.userDetails.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import in.juspay.model.Order;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.axisbank.transit.core.shared.constants.UtilsConstants.COUNTRY_CODE_WITHOUT_PLUS;

@Slf4j
@RestController
@RequestMapping(ApiConstants.BASE_URI+ApiConstants.TRANSIT_CARD)
public class TransitCardController {

    @Autowired
    private TransitCardClient transitCardClient;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private TransitCardTxnService transitCardTxnService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PaymentService paymentService;


    @PostMapping(TransitCardAPIConstants.CARD_VERIFICATION_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<JsonNode>> cardVerification(@RequestBody CardVerificationRespRequest
                                                                           cardVerificationResp) throws Exception {
        log.info("Request receive for card Verification");
        JsonNode cardVerificationRespResponse = transitCardClient.cardVerificationResp(cardVerificationResp);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, cardVerificationRespResponse);
    }


    @GetMapping(TransitCardAPIConstants.GET_ALL_LIMIT_AND_BALANCE_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<JsonNode>> fetchCardLimitAndBal() throws Exception {
        log.info("request receive for fetching all card limit and balance info");
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        CardDetailsDAO cardDetailsDAO = authenticationDAO.getCardDetailsDAO();
        GetCardAllLimitAndBalanceInfoRequest getCardAllLimitAndBalanceInfoRequest =
                transitCardTxnService.getCardAllLimitAndBalanceInfoRequest(cardDetailsDAO);
        JsonNode jsonNode = transitCardClient.getCardAllLimitAndBalanceInfo(getCardAllLimitAndBalanceInfoRequest);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,jsonNode);
    }

    @PostMapping(TransitCardAPIConstants.TOPUP_TO_PREPAID_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<Order>> topToPrepaid(@RequestBody TopupRequest request) throws Exception {
        log.info("Request receive for top-up account");
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        Order order = transitCardTxnService.createTopupRequest(authenticationDAO, request);
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,order);
    }

    @PostMapping(TransitCardAPIConstants.BLOCK_CARD_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> blockCard(@RequestBody BlockCardRequest blockCardRequest) throws Exception {
        log.info("Request receive for blocking the Card");
        BlockCardDTO blockCardDTO = transitCardTxnService.blockOrUnblockTransitCard(blockCardRequest);
        if(blockCardDTO ==null) {
            return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE,"Fail to block or unblock card");
        }
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, blockCardDTO);
    }

    @PostMapping(TransitCardAPIConstants.LINK_CARD_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<String>> linkCard(@RequestBody LinkCardRequest linkCardRequest) throws Exception {
        if(linkCardRequest.getMobileNo()!=null && !linkCardRequest.getMobileNo().equals("")){
            // add 91 prefix for mobile numbers
            linkCardRequest.setMobileNo(COUNTRY_CODE_WITHOUT_PLUS+linkCardRequest.getMobileNo());
        }
        log.info("Request receive to link card to Transit App");
        AuthenticationDAO authenticationDAO = transitCardTxnService.linkCardService(linkCardRequest);
        if(authenticationDAO !=null && authenticationDAO.getCardDetailsDAO() !=null) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, TransitCardAPIConstants.CARD_LINK_SUCCESSFULLY);
        }

        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, TransitCardAPIConstants.CARD_LINK_FAILED);
    }


    @GetMapping(TransitCardAPIConstants.GET_TRANSIT_CARD_TRANSACTIONS)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<TransitCardTransactionDTO>>> fetchRecentTransitCardTxns(
            @RequestParam(name = "pageNo") int pageNo, @RequestParam(name = "pageSize") int pageSize,
            @RequestParam(name = "txn_type") String txnType) throws Exception {
        log.info("Request receive for fetching recent Txn from Kochi1 Card");
        List<TransitCardTransactionDTO> transitCardTxnDAOS = transitCardTxnService.getTransitCardTransactions(pageNo,pageSize,
                txnType);
        if(transitCardTxnDAOS ==null) {
            return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE,"Fail to fetch recent Kochi1 card txns");
        }

        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transitCardTxnDAOS);
    }

    @GetMapping(TransitCardAPIConstants.GET_TRANSIT_CARD_INFO)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<Object>> getTransitCardInfo() throws Exception {
        log.info("Request receive for fetching Kochi1 Card Info.");
        TransitCardInfoDTO transitCardInfoDTO = transitCardTxnService.getTransitCardInfo();
        if(transitCardInfoDTO ==null) {
            return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE,"Fail to get Kochi1 card details");
        }

        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transitCardInfoDTO);
    }

    @GetMapping(TransitCardAPIConstants.GET_CARD_ALL_TXN_URL)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<JsonNode>> getCardAllTransaction(
            @RequestParam(name = "take") String take, @RequestParam(name = "skip") String skip,
            @RequestParam(name = "startDate") String startDate,@RequestParam(name = "endDate") String endDate,
            @RequestParam(name = "orderBy") String orderBy
    ) throws Exception {
        log.info("Request receive for fetching Card All Transaction.");
        JsonNode getCardAllTxns = transitCardTxnService.getCardAllTransactions(take,skip,startDate,endDate, orderBy);
        if(getCardAllTxns==null) {
            BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE,"Failed to get Card All Transactions");
        }
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE,getCardAllTxns);
    }

}
