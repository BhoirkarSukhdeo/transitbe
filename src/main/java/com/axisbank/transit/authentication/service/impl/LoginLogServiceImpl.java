package com.axisbank.transit.authentication.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.LoginLog;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.repository.LoginLogRepository;
import com.axisbank.transit.authentication.service.LoginLogService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGIN_REQUEST_TYPE;
import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGOUT_REQUEST_TYPE;

@Slf4j
@Service
public class LoginLogServiceImpl implements LoginLogService {
    @Autowired
    LoginLogRepository loginLogRepository;
    @Autowired
    AuthenticationRepository authenticationRepository;
    @Override
    public void addLoginLog(AuthenticationDAO authenticationDAO, String loginType, String loginStatus) {
        log.info("Adding user login log with login type: {}, loginStatus:{}", loginType, loginStatus);
        addLoginLog(authenticationDAO, loginType, loginStatus, LOGIN_REQUEST_TYPE);
    }

    @Override
    public void addLoginLog(String username, String loginType, String loginStatus) {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByMobileAndIsActive(username, true);
        addLoginLog(authenticationDAO, loginType, loginStatus);
    }


    @Override
    public void addLogoutLog(AuthenticationDAO authenticationDAO, String loginType, String loginStatus) {
        log.info("Adding user logout log with login type: {}, status:{}", loginType, loginStatus);
        addLoginLog(authenticationDAO, loginType, loginStatus, LOGOUT_REQUEST_TYPE);
    }

    @Override
    public void addLogoutLog(String username, String loginType, String loginStatus) {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByMobileAndIsActive(username, true);
        addLogoutLog(authenticationDAO, loginType, loginStatus);
    }

    private void addLoginLog(AuthenticationDAO authenticationDAO, String loginType, String loginStatus, String requestType){
        LoginLog loginLog = new LoginLog(CommonUtils.generateRandomString(30), loginType, loginStatus, authenticationDAO, requestType);
        loginLogRepository.save(loginLog);
    }
}
