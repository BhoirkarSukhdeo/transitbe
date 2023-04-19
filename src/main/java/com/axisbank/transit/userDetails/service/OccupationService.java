package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.userDetails.model.DTO.OccupationDTO;

import java.util.List;

public interface OccupationService {
    List<OccupationDTO> getAllOccupations() throws Exception;
}
