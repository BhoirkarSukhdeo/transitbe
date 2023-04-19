package com.axisbank.transit.transitCardAPI.controller;

import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.transitCardAPI.model.DTO.CardLimitsDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.LimitTypeDetailDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.UpdateCardTxnLimitDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.UpdateChipBalanceResponseDTO;
import com.axisbank.transit.transitCardAPI.service.TransitCardLimitService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ApiConstants.BASE_URI+ApiConstants.CARD_LIMIT_URI)
public class TransitCardLimitController {

    @Autowired
    TransitCardLimitService transitCardLimitService;

    @GetMapping("")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<CardLimitsDTO>> getLimits() throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transitCardLimitService.getLimits());
    }

    @PostMapping("/update-chip-limit")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<UpdateChipBalanceResponseDTO>> updateCardChipLimit(@RequestParam(name = "amount") double amount) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transitCardLimitService.updateCardChipLimit(amount));
    }

    @PostMapping("/update-txn-limit")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true,
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<BaseResponse<LimitTypeDetailDTO>> updateCardTxnLimit(@RequestBody UpdateCardTxnLimitDTO updateCardTxnLimitDTO) throws Exception{
        return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, transitCardLimitService.updateCardTxnLimit(updateCardTxnLimitDTO));
    }
}
