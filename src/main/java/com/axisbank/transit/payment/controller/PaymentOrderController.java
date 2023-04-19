package com.axisbank.transit.payment.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.payment.constants.PaymentConstants;
import com.axisbank.transit.payment.model.response.PaymentTransactionStatus;
import com.axisbank.transit.payment.service.PaymentService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(ApiConstants.BASE_URI+ApiConstants.PAYMENT_URI)
public class PaymentOrderController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/payment-status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<PaymentTransactionStatus>> getPaymentStatus(@RequestParam(name = "orderId") String orderId) throws Exception{
        PaymentTransactionStatus transactionSuccessData = paymentService.getPaymentStatus(orderId);
        if (transactionSuccessData != null ) {
            return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transactionSuccessData);
        }
        return BaseResponseType.errorResponse(TransitAPIConstants.API_FAIL_CODE, PaymentConstants.GET_TRANSACTION_SUCCESS_DATA_FAIL_MSG);
    }
}
