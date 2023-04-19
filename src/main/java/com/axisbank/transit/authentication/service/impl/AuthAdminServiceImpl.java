package com.axisbank.transit.authentication.service.impl;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.model.DTO.AdminUserDTO;
import com.axisbank.transit.authentication.model.DTO.UserDetailsAdminDTO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.repository.RoleRepository;
import com.axisbank.transit.authentication.service.AuthAdminService;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import com.axisbank.transit.userDetails.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthAdminServiceImpl implements AuthAdminService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserUtil userUtil;

    @Override
    public List<AdminUserDTO> getAllAdminUsers() {
        log.info("Request receive in AuthAdminServiceImpl class");
        List<AdminUserDTO> adminUserDTOS = new ArrayList<>();
        List<AuthenticationDAO> authenticationDAOS = authenticationRepository.findAllByUserTypeNotLike(TransitCardAPIConstants.TRANSIT_USER);
        log.debug("List of authentications Entities: {}", authenticationDAOS);
        for (AuthenticationDAO authenticationDAO : authenticationDAOS) {
            DAOUser daoUser = authenticationDAO.getDaoUser();
            SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
            AdminUserDTO adminUserDTO = modelMapper.map(daoUser, AdminUserDTO.class);
            adminUserDTO.setUserType(authenticationDAO.getUserType());
            adminUserDTO.setMobile(authenticationDAO.getMobile());
            adminUserDTO.setEmail(authenticationDAO.getEmail());
            adminUserDTO.setRoles(authenticationDAO.getRolesList());
            adminUserDTO.setLastlogin(sessionDAO!=null?sessionDAO.getLastApiAccessTime():CommonUtils.currentDateTime());
            adminUserDTO.setActive(authenticationDAO.getActive());
            adminUserDTO.setUsername(authenticationDAO.getUserName());
            adminUserDTOS.add(adminUserDTO);
        }
        return adminUserDTOS;
    }

    @Override
    @Transactional
    public void createUser(UserDetailsAdminDTO userDetailsAdminDTO) throws Exception {
        log.info("Request received in create user for admin portal: ");
        try {
            if (userDetailsAdminDTO.getUsername().contains("@axis")) {
                throw new Exception("username should not contain @axisbank/ @axisb");
            }
            checkIfUserExists(userDetailsAdminDTO.getUsername());
            AuthenticationDAO authenticationDAO = new AuthenticationDAO();
            authenticationDAO.setUserName(userDetailsAdminDTO.getUsername());
            authenticationDAO.setMobile(userDetailsAdminDTO.getMobile());
            authenticationDAO.setUserType(userDetailsAdminDTO.getUserType());
            authenticationDAO.setEmail(userDetailsAdminDTO.getEmail());
            authenticationDAO.setOtpVerification(true);

            if (userDetailsAdminDTO.getRoles().size() > 0) {
                List<Role> roleList = roleRepository.findAllByNameInAndIsActive(userDetailsAdminDTO.getRoles(), true);
                authenticationDAO.setRoles(roleList);
            }

            DAOUser daoUser = new DAOUser();
            daoUser.setUserId(CommonUtils.generateRandomString(30));
            daoUser.setFirstName(userDetailsAdminDTO.getFirstName());
            daoUser.setMiddleName(userDetailsAdminDTO.getMiddleName());
            daoUser.setLastName(userDetailsAdminDTO.getLastName());
            daoUser.setGender(userDetailsAdminDTO.getGender());
            daoUser.setDob(userDetailsAdminDTO.getDob());
            daoUser.setAuthenticationDAO(authenticationDAO);
            authenticationDAO.setDaoUser(daoUser);
            authenticationRepository.save(authenticationDAO);

        } catch (Exception exception) {
            log.error("Error in create-user: {}",exception.getMessage());
            throw exception;
        }
    }

    @Override
    public List<String> getAllRoles() throws Exception {
        log.info("Request Recieved in getAllRoles method: ");
        try {
            List<Role> roles = roleRepository.findAllByIsActive(true);
            return roles.stream().map(Role::getName).collect(Collectors.toList());
        } catch (Exception exception) {
            log.error("Exception in get ALL roles: {}", exception.getMessage());
            throw new Exception("Error in getting Roles, Please try again later");
        }
    }

    @Override
    @Transactional
    public void updateUser(UserDetailsAdminDTO userDetailsAdminDTO) throws Exception {
        log.info("Request received in update user for admin portal: ");
        String username = userUtil.getLoggedInUserName();
        try {
            DAOUser daoUser = userRepository.findByUserId(userDetailsAdminDTO.getUserId());
            if (daoUser == null) {
                log.error("User not present in db");
                throw new Exception("User not present with given user Id");
            }
            AuthenticationDAO authenticationDAO = daoUser.getAuthenticationDAO();
            authenticationDAO.setMobile(userDetailsAdminDTO.getMobile());
            authenticationDAO.setUserType(userDetailsAdminDTO.getUserType());
            authenticationDAO.setOtpVerification(true);
            if(username.equalsIgnoreCase(authenticationDAO.getUserName()) && !userDetailsAdminDTO.isActive()){
                throw new Exception("You cannot mark yourself as inactive");
            }
            authenticationDAO.setActive(userDetailsAdminDTO.isActive());

            if (userDetailsAdminDTO.getRoles().size() > 0) {
                List<Role> roleList = roleRepository.findAllByNameInAndIsActive(userDetailsAdminDTO.getRoles(), true);
                authenticationDAO.setRoles(roleList);
            }

            daoUser.setFirstName(userDetailsAdminDTO.getFirstName());
            daoUser.setMiddleName(userDetailsAdminDTO.getMiddleName());
            daoUser.setLastName(userDetailsAdminDTO.getLastName());
            daoUser.setGender(userDetailsAdminDTO.getGender());
            daoUser.setDob(userDetailsAdminDTO.getDob());
            daoUser.setAuthenticationDAO(authenticationDAO);
            daoUser.setActive(userDetailsAdminDTO.isActive());
            authenticationDAO.setDaoUser(daoUser);
            authenticationRepository.save(authenticationDAO);

        } catch (Exception exception) {
            log.error("Error in update-user: {}",exception.getMessage());
            throw new Exception("Error in updating User Details, Pls try again after sometime");
        }
    }

    private void checkIfUserExists(String username) throws Exception {
        AuthenticationDAO authenticationDAO = authenticationRepository.findByUserNameIgnoreCase(username);
        if (authenticationDAO !=null ) {
            throw new Exception("User is already present.");
        }
    }
}
