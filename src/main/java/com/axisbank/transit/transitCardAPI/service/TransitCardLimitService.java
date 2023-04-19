package com.axisbank.transit.transitCardAPI.service;

import com.axisbank.transit.transitCardAPI.model.DTO.CardLimitsDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.LimitTypeDetailDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.UpdateCardTxnLimitDTO;
import com.axisbank.transit.transitCardAPI.model.DTO.UpdateChipBalanceResponseDTO;

public interface TransitCardLimitService {
    CardLimitsDTO getLimits() throws Exception;

    UpdateChipBalanceResponseDTO updateCardChipLimit(double amount) throws Exception;

    LimitTypeDetailDTO updateCardTxnLimit(UpdateCardTxnLimitDTO updateCardTxnLimitDTO) throws Exception;
}
