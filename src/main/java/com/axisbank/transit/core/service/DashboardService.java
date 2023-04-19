package com.axisbank.transit.core.service;

import com.axisbank.transit.core.model.response.DashboardResponseDTO;

public interface DashboardService {
    public DashboardResponseDTO getDasboardDetails() throws Exception;
}
