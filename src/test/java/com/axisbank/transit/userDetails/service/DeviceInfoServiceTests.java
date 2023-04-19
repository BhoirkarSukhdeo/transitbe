package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.userDetails.model.DAO.DeviceInfo;
import com.axisbank.transit.userDetails.model.DTO.DeviceInfoDTO;
import com.axisbank.transit.userDetails.service.impl.DeviceInfoServiceImpl;
import com.axisbank.transit.userDetails.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;

public class DeviceInfoServiceTests extends BaseTest {
    private AuthenticationDAO authenticationDAO;

    @Mock
    UserUtil userUtil;

    @Mock
    AuthenticationRepository authenticationRepository;

    @InjectMocks
    @Autowired
    DeviceInfoServiceImpl deviceInfoService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authenticationDAO = new AuthenticationDAO();
        authenticationDAO.setMobile("2233771199");
        authenticationDAO.setEmail("pradeeep123@gmail.com");
        authenticationDAO.setUserName("2233771199");
        authenticationDAO.setOtpVerification(false);
    }

    @Test
    public void updateDeviceInfoTest() throws Exception{
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);
        when(authenticationRepository.save(authenticationDAO)).thenReturn(authenticationDAO);
        DeviceInfoDTO deviceInfo = new DeviceInfoDTO();
        deviceInfo.setOsVersion("11.0");
        deviceInfo.setAppVersion("1.2");
        deviceInfo.setOsType("android");
        deviceInfo.setFcmToken("token123");

        deviceInfoService.updateDeviceInfo(deviceInfo);
        Assert.assertNotNull(authenticationDAO.getDeviceInfo());
    }

    @Test
    public void getDeviceInfoTest() throws Exception{

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setOsVersion("11.0");
        deviceInfo.setAppVersion("1.2");
        deviceInfo.setOsType("android");
        deviceInfo.setFcmToken("token123");
        authenticationDAO.setDeviceInfo(deviceInfo);
        when(userUtil.getAuthObject()).thenReturn(authenticationDAO);

        Assert.assertNotNull(deviceInfoService.getDeviceInfo());
    }
}
