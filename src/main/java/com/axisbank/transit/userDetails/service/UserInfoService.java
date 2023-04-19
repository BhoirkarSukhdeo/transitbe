package com.axisbank.transit.userDetails.service;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.model.DTO.UserConfigDTO;
import com.axisbank.transit.userDetails.model.DTO.UserDetailsDTO;
import com.axisbank.transit.userDetails.model.DTO.UserConfigurationDTO;

public interface UserInfoService {

    public UserDetailsDTO getUserDetails(String user_id) throws Exception;

    public AuthenticationDAO updateUserDetails(UserDetailsDTO userDetails) throws Exception;

    public UserDetailsDTO getLoggedInUserDetails() throws Exception;

    public UserConfigDTO getUserConfig() throws Exception;

    public DAOUser updateUserConfiguration (UserConfigurationDTO userConfiguration) throws Exception;

}
