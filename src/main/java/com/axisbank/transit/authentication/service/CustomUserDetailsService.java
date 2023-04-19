package com.axisbank.transit.authentication.service;

import com.axisbank.transit.authentication.constants.AuthenticationConstants;
import com.axisbank.transit.authentication.exceptions.BlockedUserException;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DAO.Role;
import com.axisbank.transit.authentication.model.DAO.SessionDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.shared.utils.OtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private OtpUtils otpUtils;

	@Autowired
	private AuthService authService;

	/**
	 * locate the user from database based on the username
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public User loadUserByUsername(String username) throws BlockedUserException, UsernameNotFoundException, MpinBlockedUserException {

		AuthenticationDAO authenticationDAO = authenticationRepository.findByUserNameIgnoreCaseAndIsActive(username, true);
		if (authenticationDAO != null) {
			SessionDAO sessionDAO = authenticationDAO.getSessionDAO();
			if (sessionDAO != null) {
				authService.checkBlockedUser(sessionDAO);
			}
			return new User(authenticationDAO.getUserName(), "", getGrantedAuthorities(authenticationDAO.getRoles()));
		}
		throw new UsernameNotFoundException("User not found with number " + username);
	}

	private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}
}
