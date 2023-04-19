package com.axisbank.transit.authentication.service;


import com.axisbank.transit.authentication.model.DTO.AdminUserDTO;
import com.axisbank.transit.authentication.model.DTO.UserDetailsAdminDTO;

import java.util.List;

public interface AuthAdminService {
    List<AdminUserDTO> getAllAdminUsers();
    void createUser(UserDetailsAdminDTO userDetailsAdminDTO) throws Exception;

    List<String> getAllRoles() throws Exception;

    void updateUser(UserDetailsAdminDTO userDetailsAdminDTO) throws Exception;
}
