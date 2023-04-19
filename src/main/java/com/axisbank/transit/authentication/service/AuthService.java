package com.axisbank.transit.authentication.service;

import com.axisbank.transit.authentication.constants.RegistrationType;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.model.DTO.AuthDTO;
import com.axisbank.transit.authentication.model.DTO.SetMpinDTO;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;

import java.time.LocalDateTime;

public interface AuthService {

    public AuthenticationDAO saveUser(AuthDTO user) throws Exception;

    public void processFullName(String fullName, DAOUser newUser);

    public void enableOtpVerication(String mobile, LocalDateTime currentDateTime);

    public void setUserRole(AuthenticationDAO savedUser);

    public void deleteUser(String mobileNumber) throws Exception;

    public void saveRefreshTokenAndLastAccessTime(String refreshToken, String mobile);

    public void checkRefreshToken(String refreshToken, String username) throws Exception;

    void deleteUser(AuthenticationDAO authenticationDAO) throws Exception;

    AuthenticationDAO setMpin(SetMpinDTO setMpinDTO) throws Exception;

    void checkMpin(String userName, String mpin) throws Exception;

    void checkBlockedUser(SessionDAO sessionDAO) throws MpinBlockedUserException;

    AuthenticationDAO getTransitCardAndValidation(AuthDTO authDTO, RegistrationType registrationType) throws  Exception;

    Boolean confirmCifId(AuthDTO authDTO) throws Exception;
    String generateOTK(String refreshToken, String username) throws Exception;

    public AuthenticationDAO getMobileNumber(String userName);
}
