package com.axisbank.transit.userDetails.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import com.axisbank.transit.userDetails.model.DTO.DeviceInfoDTO;
import com.axisbank.transit.userDetails.service.DeviceInfoService;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class DeviceInfoServiceImpl implements DeviceInfoService {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserUtil userUtil;

    @Autowired
    AuthenticationRepository authenticationRepository;

    public void updateDeviceInfo(DeviceInfoDTO deviceInfoDTO) throws Exception {
        log.info("Request received in updateDeviceInfo method");
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            DeviceInfo deviceInfo = null;
            if (authenticationDAO.getDeviceInfo() == null ) {
                deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceId(CommonUtils.generateRandomString(30));
                deviceInfo.setAuthenticationDAO(authenticationDAO);
                authenticationDAO.setDeviceInfo(deviceInfo);
            } else {
                deviceInfo = authenticationDAO.getDeviceInfo();
            }
            if (deviceInfoDTO.getAppVersion() != null) {
                deviceInfo.setAppVersion(deviceInfoDTO.getAppVersion());
            }
            if (deviceInfoDTO.getFcmToken() != null) {
                deviceInfo.setFcmToken(deviceInfoDTO.getFcmToken());
            }
            if (deviceInfoDTO.getOsType() != null) {
                deviceInfo.setOsType(deviceInfoDTO.getOsType());
            }
            if (deviceInfoDTO.getOsVersion() != null) {
                deviceInfo.setOsVersion(deviceInfoDTO.getOsVersion());
            }
            authenticationRepository.save(authenticationDAO);

        } catch (Exception exception) {
            log.error("Exception in updateDeviceInfo: {}", exception.getMessage());
            throw exception;
        }
    }

    public DeviceInfoDTO getDeviceInfo() throws Exception {
        log.info("Request received in getDeviceInfo method");
        DeviceInfoDTO deviceInfoDTO = null;
        try {
            AuthenticationDAO authenticationDAO = userUtil.getAuthObject();
            DeviceInfo deviceInfo = authenticationDAO.getDeviceInfo();
            deviceInfoDTO = modelMapper.map(deviceInfo, DeviceInfoDTO.class);
        } catch (Exception exception) {
            log.error("Exception in getDevice Info: {}", exception.getMessage());
            throw exception;
        }
        return deviceInfoDTO;
    }
}
