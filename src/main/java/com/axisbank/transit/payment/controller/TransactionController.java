package com.axisbank.transit.payment.controller;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.payment.model.DTO.TransactionAdminDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionDetailsDTO;
import com.axisbank.transit.payment.model.DTO.TransactionFiltersDTO;
import com.axisbank.transit.payment.service.TransactionService;
import com.axisbank.transit.userDetails.util.UserUtil;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(ApiConstants.BASE_URI+ApiConstants.TRANSACTION_URI)
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserUtil userUtil;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<List<TransactionDetailsDTO>>> getAllTransactions(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "category", defaultValue = "%") String category,
            @RequestParam(name = "paymentType", defaultValue = "%") String paymentType,
            @RequestParam(name = "from", defaultValue = "01/01/1970") String from,
            @RequestParam(name = "to", defaultValue = "") String to,
            @RequestParam(name = "status", defaultValue = "%") String status) throws Exception {
        String format = "dd/MM/yyyy HH:mm";
        Date fromDate = new SimpleDateFormat(format).parse(from + " 00:00");
        Date toDate = new SimpleDateFormat(format).parse((to.equals("")) ? CommonUtils.getCurrentDateTime(format):
                to + " 23:59");
        AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
        return BaseResponseType.successfulResponse(transactionService.getAllTxn(pageNo, pageSize, authenticationDAO,
                category, paymentType, status, fromDate, toDate));
    }

    @GetMapping(ApiConstants.FILTERS)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<TransactionFiltersDTO>> getAllFilters() throws Exception {
        return BaseResponseType.successfulResponse(transactionService.getAllFilters());
    }



    @GetMapping(ApiConstants.TRANSACTIONS_LIST_URI)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<TransactionAdminDetailsDTO>> getAllTransactions(@RequestParam(name = "startDate") String startDate,
                                                                                       @RequestParam(name = "endDate") String endDate ,
                                                                                       @RequestParam(name = "status",required = false, defaultValue ="DONE,FAILED") String status,
                                                                                       @RequestParam(name = "type",required = false, defaultValue = "%") String type ) throws Exception {
       return BaseResponseType.successfulResponse(transactionService.getAllTransactions(CommonUtils.getDateForStart(startDate), CommonUtils.getDateForEnd(endDate), status.toUpperCase(), type.toUpperCase()));
    }
}
