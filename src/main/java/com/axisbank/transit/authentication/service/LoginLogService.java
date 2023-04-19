package com.axisbank.transit.authentication.service;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;

public interface LoginLogService {
    void addLoginLog(AuthenticationDAO authenticationDAO, String loginType, String loginStatus);
    void addLoginLog(String username, String loginType, String loginStatus);
    void addLogoutLog(AuthenticationDAO authenticationDAO, String loginType, String loginStatus);
    void addLogoutLog(String username, String loginType, String loginStatus);
}
