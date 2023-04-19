package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.userDetails.model.DTO.DeviceInfoDTO;

public interface DeviceInfoService {
    DeviceInfoDTO getDeviceInfo() throws Exception;

    void updateDeviceInfo(DeviceInfoDTO deviceInfoDTO) throws Exception;

}
