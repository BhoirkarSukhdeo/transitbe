package com.axisbank.transit.payment.service;

import com.axisbank.transit.payment.model.DAO.TransactionDAO;
import com.axisbank.transit.payment.model.response.PaymentTransactionStatus;
import com.axisbank.transit.transitCardAPI.model.request.TopupRequest;
import in.juspay.exception.*;
import in.juspay.model.Customer;
import in.juspay.model.Order;

import java.util.Map;

public interface PaymentService {

     Customer createCustomer(String mobile, String userId) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException;
     Customer getCustomer(String customerId) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException;
     Customer updateCustomer(String customerId, Map params) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException;
     Order createOrder(TopupRequest topupRequest, String customerId, String txnType) throws AuthorizationException, APIException,
             AuthenticationException, InvalidRequestException, APIConnectionException, RuntimeException;
     Order orderStatus(String orderId, String txnType) throws AuthorizationException, APIException, AuthenticationException,
             InvalidRequestException, APIConnectionException, RuntimeException;
     Order updateOrder(String orderId, Map params) throws AuthorizationException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException;
     Order refundOrder(String orderId, String referenceId, Double amount, String txnType) throws AuthorizationException,
             APIException, AuthenticationException, InvalidRequestException, APIConnectionException, RuntimeException;
     boolean processTransaction(String orderId) throws Exception;
     PaymentTransactionStatus getPaymentStatus(String orderId) throws Exception;
     TransactionDAO processBookTicketTxnKmrl(String orderId, TransactionDAO transactionDAO) throws Exception;
}
