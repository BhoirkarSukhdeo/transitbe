package com.axisbank.transit.bus.service;

import com.axisbank.transit.bus.model.DTO.BusFareChartDTO;

import java.util.List;

public interface BusFareService {
    List<BusFareChartDTO> getAllRouteFares(int page, int size) throws Exception;
}
