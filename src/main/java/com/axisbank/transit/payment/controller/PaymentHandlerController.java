package com.axisbank.transit.payment.controller;

import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping(ApiConstants.BASE_URI+ApiConstants.PAYMENT_URI)
public class PaymentHandlerController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping(ApiConstants.PAYMENT_RESPONSE_HANDLER)
    public String paymentResponesHandler(@RequestParam(name = "order_id") String orderId,
                                       @RequestParam(name = "status") String status,
                                       @RequestParam(name = "status_id") String statusId,
                                       @RequestParam(name = "signature") String signature,
                                       @RequestParam(name = "signature_algorithm") String signatureAlgorithm,
                                       Model model ) throws Exception {
        log.info("Request receive for payment status update");
        log.info("OrderId:{}, statusId:{}  ", orderId, statusId);
        try{
            boolean is_success = paymentService.processTransaction(orderId);
            if(is_success) return "payment_handler";
        } catch (Exception ex ){
           log.error("Failed to save transaction due to: {}",ex.getMessage());
        }
        return "payment_handler_error";
    }

}
