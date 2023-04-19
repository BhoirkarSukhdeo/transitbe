package com.axisbank.transit.payment.service.impl;

import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.kmrl.client.KmrlTicketingClient;
import com.axisbank.transit.kmrl.constant.TransitTicketStatus;
import com.axisbank.transit.kmrl.model.DAO.TicketDAO;
import com.axisbank.transit.kmrl.model.DTO.InsQrCodeTicketMobileDTO;
import com.axisbank.transit.payment.constants.OrderStatus;
import com.axisbank.transit.payment.constants.PaymentConstants;
import com.axisbank.transit.payment.constants.ServiceProviderConstant;
import com.axisbank.transit.payment.constants.TransactionStatus;
import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.model.response.PaymentTransactionStatus;
import com.axisbank.transit.payment.repository.TransactionRepository;
import com.axisbank.transit.payment.service.PaymentService;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.transitCardAPI.TransitCardClient.TransitCardClient;
import com.axisbank.transit.transitCardAPI.constants.TxnStatus;
import com.axisbank.transit.transitCardAPI.constants.TxnType;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import com.axisbank.transit.transitCardAPI.model.request.topupToPrepaid.TopupToPrepaid;
import com.axisbank.transit.transitCardAPI.service.TransitCardTxnService;
import com.fasterxml.jackson.databind.JsonNode;
import in.juspay.exception.*;
import in.juspay.model.Customer;
import in.juspay.model.Order;
import in.juspay.model.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.axisbank.transit.kmrl.constant.KmrlSecondaryTicketStatus.UNUSED;
import static com.axisbank.transit.kmrl.constant.KmrlTicketTypes.TICKET_TYPE_MAP;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private TransitCardTxnService transitCardTxnService;

    @Autowired
    private TransitCardClient transitCardClient;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    KmrlTicketingClient kmrlTicketingClient;

    @Autowired
    private TransactionRepository transactionRepository;


    @Value("${response.handler.url}")
    private String paymentResponeHandlerUrl;

    @Value("${kmrl.block.book}")
    private boolean blockBookTicket;

    @Value("${juspay.api_key}")
    private String topUpPgAPIKey;
    @Value("${juspay.merchant_id}")
    private String topUpPgMID;


    @Value("${juspay.book_ticket.api_key}")
    private String bookTicketPgAPIKey;
    @Value("${juspay.book_ticket.merchant_id}")
    private String bookTicketPgMID;


    /**
     * This Function takes in two params, @param mobile and @param userId and create new customer
     * @param mobile
     * @param userId
     * @return Customer
     * @throws AuthorizationException,APIException,AuthenticationException,InvalidRequestException,APIConnectionException
     */

    @Override
    public Customer createCustomer(String mobile, String userId) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
//     JusPay Customer.create() method call
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(PaymentConstants.OBJECT_REFERENCE_ID,userId);
        params.put(PaymentConstants.MOBILE_NUMBER, mobile);
        return Customer.create(params);
    }


    /**
     * This Function takes in @param userId and  fetch customer details
     * @param customerId
     * @param customerId
     * @return Customer
     * @throws AuthorizationException,APIException,AuthenticationException,InvalidRequestException,APIConnectionException
     */

    @Override
    public Customer getCustomer(String customerId) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
//     JusPay Customer.create() method call
        return Customer.get(customerId);
    }

    @Override
    public Customer updateCustomer(String customerId, Map params) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
//  JusPay Customer.update() method call
        return Customer.update(customerId,params);
    }



    /**
     * This Function takes in @param topupRequest and @param customerId  and create new Order
     * @param topupRequest
     * @param customerId
     * @return Customer
     * @throws AuthorizationException,APIException,AuthenticationException,InvalidRequestException,APIConnectionException
     */

    @Override
    public Order createOrder(TopupRequest topupRequest, String customerId, String txnType) throws AuthorizationException, APIException,
            AuthenticationException, InvalidRequestException, APIConnectionException, RuntimeException {
        Map<String, Object> params = new LinkedHashMap<>();
        RequestOptions rqo = getRequestOptions(txnType);
        params.put(PaymentConstants.AMOUNT, topupRequest.getAmount());
        params.put(PaymentConstants.ORDER_ID, CommonUtils.generateRandInt(10));
        params.put(PaymentConstants.CURRENCY,"INR");
        params.put(PaymentConstants.CUSTOMER_ID,customerId);
        params.put(PaymentConstants.RETURN_URL, paymentResponeHandlerUrl);
        Order order = Order.create(params, rqo);
        order.setMerchantId(rqo.getMerchantId());
        return order;
    }


    @Override
    public Order orderStatus(String orderId, String txnType) throws AuthorizationException, APIException, AuthenticationException,
            InvalidRequestException, APIConnectionException, RuntimeException {
        RequestOptions rqo = getRequestOptions(txnType);
        return Order.status(orderId, rqo);
    }

    @Override
    public Order updateOrder(String orderId, Map params) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        return Order.update(orderId, params);
    }

    @Override
    public Order refundOrder(String orderId, String referenceId, Double amount, String txnType) throws AuthorizationException,
            APIException, AuthenticationException, InvalidRequestException, APIConnectionException, RuntimeException {
        RequestOptions rqo = getRequestOptions(txnType);
        Map<String, Object> params =new LinkedHashMap<>();
        params.put("unique_request_id", referenceId);
        params.put("amount", amount);
        return Order.refund(orderId, params, rqo);
    }

    @Override
    public boolean processTransaction(String orderId) throws Exception{
        Order order = null;
        TransactionDAO transactionDAO = transactionService.getTnxByOrderId(orderId);
        try{
            order = orderStatus(orderId, transactionDAO.getTxnType().toUpperCase());
        } catch (Exception ex){
            log.error("Error while getting order status:{}",ex.getMessage());
            // TODO refund if failed
            throw new Exception("Failed to fetch Order details");
        }
        if (order==null){
            throw new Exception("No Order Found");
        }
        if (transactionDAO.getPspStatus().equalsIgnoreCase(OrderStatus.CHARGED.toString())){
            throw new Exception("This Transaction is already finished");
        }
        transactionDAO.setPspStatus(order.getStatus());
        transactionDAO.setPspTxnId(order.getTxnId());
        transactionDAO.setPspPaymentMethod(order.getPaymentMethod());
        transactionDAO.setPspPaymentMethodType(order.getPaymentMethodType());
        transactionDAO.setTxnCompletedOn(new Date(CommonUtils.getCurrentTimeMillis()));
        if (!order.getStatus().equals(OrderStatus.CHARGED.toString())){
            // TODO Get status from map
            transactionDAO.setFinalTxnStatus(TransactionStatus.FAILED.toString());
            transactionService.saveTxn(transactionDAO);
            return false;
        }
        switch (transactionDAO.getServiceProvider()){
            case ServiceProviderConstant.TRANSIT_CARD:
                try {
                    transactionDAO = processTopupTransitcardTxn(orderId, transactionDAO);
                } catch (Exception ex) {
                    transactionService.createRefund(transactionDAO);
                    log.error("Failed to topup card: {}",ex.getMessage());
                    return false;
                }
                break;
            case ServiceProviderConstant.KMRL:
                try {
                    transactionDAO = processBookTicketTxnKmrl(orderId, transactionDAO);
                } catch (Exception ex) {
                    transactionService.createRefund(transactionDAO);
                    log.error("Failed to Book Ticket: {}",ex.getMessage());
                    return false;
                }
                break;
        }
        transactionService.saveTxn(transactionDAO);
        return true;
    }

    private TransactionDAO processTopupTransitcardTxn(String orderId, TransactionDAO transactionDAO) throws Exception{
        String transitCardNo = transitCardTxnService.getTransitCardNo(transactionDAO.getSpRefId());
        TopupRequest topupRequest = new TopupRequest(String.valueOf(transactionDAO.getAmount()));
        topupRequest.setSrcRefId(transactionDAO.getOrderId());
        TopupToPrepaid topupToPrepaid = transitCardTxnService.getTopupToPrepaidRequest(
                transitCardNo, topupRequest);
        try {
            JsonNode transactionstatus = transitCardClient.topupToPrepaidResponse(topupToPrepaid);
            JsonNode transitTxnData = transactionstatus.get("TopupToPrepaidResponse").get("ResponseBody")
                    .get("TopupToPrepaidResult");
            String result = transitTxnData.get("Result").asText("Failed");
            transactionDAO.setSpStatus(result);
            if(!result.equalsIgnoreCase("success")){
                transactionDAO.setFinalTxnStatus(TransactionStatus.FAILED.toString());
                throw new Exception("Unable to topup Kochi1 card");
            }
            transactionDAO.setSpTxnId(transitTxnData.get("TxnReferanceId").asText());
            transactionDAO.setFinalTxnStatus(TransactionStatus.DONE.toString());

        } catch (Exception ex){
            transactionDAO.setFinalTxnStatus(TransactionStatus.FAILED.toString());
            transactionDAO.setSpStatus(TxnStatus.FAILED.toString());
            log.error("Exception in processTopupTransitcardTxn: {}", ex.getMessage());
            throw new Exception("Unable to topup Kochi1 card");
        }
        return transactionDAO;
    }

    @Override
    public TransactionDAO processBookTicketTxnKmrl(String orderId, TransactionDAO transactionDAO) throws Exception {
        TicketDAO ticketDAO = transactionDAO.getTicketDAO();
        InsQrCodeTicketMobileDTO insQrCodeTicketMobileDTO = new InsQrCodeTicketMobileDTO();
        String ticketActiveFrom = CommonUtils.startDayTime("yyyy-MM-dd");
        String ticketActiveTo = CommonUtils.endDayTime("yyyy-MM-dd");
        int ticketTypeId = TICKET_TYPE_MAP.get(ticketDAO.getTicketType()).getTicketTypeId();
        insQrCodeTicketMobileDTO.setActiveFrom(ticketActiveFrom);
        insQrCodeTicketMobileDTO.setActiveTo(ticketActiveTo);
        insQrCodeTicketMobileDTO.setFromId(ticketDAO.getFromMetroStation().getSetStationId());
        insQrCodeTicketMobileDTO.setToId(ticketDAO.getToMetroStation().getSetStationId());
        insQrCodeTicketMobileDTO.setExplanation("Book ticket desc");
        insQrCodeTicketMobileDTO.setPeopleCount(ticketDAO.getTravellers());
        insQrCodeTicketMobileDTO.setPrice(ticketDAO.getTicketFare());
        insQrCodeTicketMobileDTO.setTicketType(ticketTypeId);

        List<InsQrCodeTicketMobileDTO> insQrCodeTicketMobileDTOList = new ArrayList<>();
        insQrCodeTicketMobileDTOList.add(insQrCodeTicketMobileDTO);
        try {
            if(blockBookTicket)
                throw new Exception("Booking Unavailable for a moment");
            JsonNode ticket = kmrlTicketingClient.insQrCodeTicketMobile(insQrCodeTicketMobileDTOList);
            JsonNode ticketInfo = ticket.get("InsQrCodeTicketMobileResponse").get("InsQrCodeTicketMobileResult");

            String result = ticketInfo.get("description").asText();
            transactionDAO.setSpStatus(result);
            if(!result.equalsIgnoreCase("Success")){
                transactionDAO.setFinalTxnStatus(TransactionStatus.FAILED.toString());
                transactionDAO.setSpStatus("Failed");
                throw new Exception("Unable to Book Ticket");
            }
            transactionDAO.setSpStatus("Success");
            transactionDAO.setSpTxnId(ticketInfo.get("transactionId").asText());
            transactionDAO.setFinalTxnStatus(TransactionStatus.DONE.toString());
            ticketDAO.setTicketGUID(ticketInfo.get("ticketGUID").asText());
            ticketDAO.setTicketNo(ticketInfo.get("ticketNo").asText());
            ticketDAO.setDescription(ticketInfo.get("description").asText());
            ticketDAO.setTicketTransactionId(ticketInfo.get("transactionId").asText());
            ticketDAO.setTicketStatus(TransitTicketStatus.UPCOMING);
            ticketDAO.setSecondaryTicketStatus(UNUSED);
            ticketDAO.setJourneyDate(ticketActiveFrom);
            transactionDAO.setTicketDAO(ticketDAO);
            ticketDAO.setTransactionDAO(transactionDAO);

        } catch (Exception ex){
            transactionDAO.setFinalTxnStatus(TransactionStatus.FAILED.toString());
            transactionDAO.setSpStatus(TxnStatus.FAILED.toString());
            log.error("Exception in BookTicket: {}", ex.getMessage());
            throw new Exception("Unable to Book Ticket");
        }
        return transactionDAO;
    }

    @Override
    public PaymentTransactionStatus getPaymentStatus(String orderId) throws Exception {
        PaymentTransactionStatus transactionSuccessData = null;
        try {
            TransactionDAO transactionDAO = transactionService.getTnxByOrderId(orderId);
//            Update txnStatus to DONE if txnType is REFUND and Current Order status is refunded
            try {
                if(transactionDAO.getTxnType().equalsIgnoreCase(TxnType.REFUND.toString()) &&
                        transactionDAO.getFinalTxnStatus().equalsIgnoreCase(TxnStatus.INITIATED.toString())) {
                    transactionDAO = transactionService.updateRefundOrderStatus(transactionDAO);
                }
            } catch (Exception ex) {
                log.error("Failed to update jusPay order status");
            }
            transactionSuccessData = new PaymentTransactionStatus();
            transactionSuccessData.setOrderId(transactionDAO.getOrderId());
            transactionSuccessData.setPspTxnId(transactionDAO.getPspTxnId());
            transactionSuccessData.setSpTxnId(transactionDAO.getSpTxnId());
            transactionSuccessData.setAmount(String.valueOf(transactionDAO.getAmount()));
            transactionSuccessData.setPaymentMethod(transactionDAO.getPspPaymentMethod());
            transactionSuccessData.setPaidTo(transactionDAO.getServiceProvider());
            transactionSuccessData.setStatus(transactionDAO.getFinalTxnStatus());
            Date createdDateTime = transactionDAO.getCreatedAt();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(createdDateTime);
            String time = new SimpleDateFormat("HH:mm:ss").format(createdDateTime);
            transactionSuccessData.setDate(date);
            transactionSuccessData.setTime(time);


        } catch (Exception e) {
            log.error("Exception in getpaymentStatus: {}", e.getMessage());
            throw new Exception("Failed to get Transaction Success Data");
        }
        return transactionSuccessData;
    }

    private RequestOptions getRequestOptions(String type) throws RuntimeException{
        String api_key = "";
        String mid = "";
        switch (type.toUpperCase()){
            case "BOOK_TICKET":
                api_key = bookTicketPgAPIKey;
                mid = bookTicketPgMID;
                break;
            case "TOP_UP":
                api_key = topUpPgAPIKey;
                mid = topUpPgMID;
                break;
            default:
                log.error("Transaction Type: {} doesn't allow PaymentGateway transactions", type);
                throw new RuntimeException("Failed to fetch PG details for given transaction type");
        }
        RequestOptions requestOptions = RequestOptions.createDefault();
        requestOptions.withApiKey(api_key);
        requestOptions.withMerchantId(mid);
        return requestOptions;
    }
}
