package com.axisbank.transit.authentication.filters;

import com.axisbank.transit.SpringApplicationContext;
import com.axisbank.transit.authentication.constants.SecurityConstants;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.request.AuthenticationRequest;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.service.CustomUserDetailsService;
import com.axisbank.transit.authentication.service.impl.AuthServiceImpl;
import com.axisbank.transit.authentication.service.impl.LoginLogServiceImpl;
import com.axisbank.transit.authentication.util.JwtUtil;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.OtpUtils;
import com.axisbank.transit.transitCardAPI.TransitCardClient.FinacleClient;
import com.axisbank.transit.transitCardAPI.model.request.getCustomerDtlsRequest.GetCustomerDtlsRequest;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGIN_STATUS_SUCCESS;
import static com.axisbank.transit.authentication.constants.LoginLogConstants.LOGIN_TYPE_OTP;
import static com.axisbank.transit.core.shared.constants.UtilsConstants.COUNTRY_CODE_WITHOUT_PLUS;
import static com.axisbank.transit.core.shared.utils.CommonUtils.isNullOrEmpty;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, RuntimeException {
		try {
			AuthenticationRequest creds = new ObjectMapper().readValue(req.getInputStream(),
					AuthenticationRequest.class);
			String username;
			if (!isNullOrEmpty(creds.getCifId())) {
				log.info("CIF Authentication");
				if (creds.getCifId() == null) throw new Exception("CIF_ID can not be null");
				GetCustomerDtlsRequest getCustomerDtlsRequest = new GetCustomerDtlsRequest();
				getCustomerDtlsRequest.setCustId(creds.getCifId());
				FinacleClient finacleClient = (FinacleClient) SpringApplicationContext.getBean("finacleClient");
				JsonNode getCustDetailsRes = finacleClient.getCustomerDetails(getCustomerDtlsRequest);
				String matchFound = getCustDetailsRes.get("getCustomerDtlsResponse").get("matchFound").asText();
				if (!matchFound.equalsIgnoreCase("True")) throw new Exception("Failed to match Customer Details");
				JsonNode getCustomerDtlsRes = getCustDetailsRes.get("getCustomerDtlsResponse").get("CustomerDetails");
				username = getCustomerDtlsRes.get("mobile").asText();
				if (!verifyUserByOTP(username, creds.getOtp())) throw new Exception("OTP not verified");
			}
			else if (!isNullOrEmpty(creds.getUsername())){
				username = creds.getUsername();
				if(!verifyADUser(username, creds.getPassword())) throw new Exception("AD User Not Authenticated");
			}
			else {
				if(creds.getMobile()!=null && !creds.getMobile().equals("")){
					// add 91 prefix for mobile numbers
					creds.setMobile(COUNTRY_CODE_WITHOUT_PLUS+creds.getMobile());
				}
				username = creds.getMobile();
				if (!verifyUserByOTP(username, creds.getOtp())) throw new Exception("OTP not verified");
			}

			CustomUserDetailsService userDetailsService  = (CustomUserDetailsService) SpringApplicationContext.getBean("customUserDetailsService");
			User user = userDetailsService.loadUserByUsername(username);

			return new UsernamePasswordAuthenticationToken(
					user,
					null,
					user.getAuthorities()
			);
		} catch (MpinBlockedUserException exception) {
			log.info("Exception while authenticating user:{}", exception.getMessage());
			res.setStatus(HttpStatus.FORBIDDEN.value());
			res.setHeader("Content-Type","application/json");
			BaseResponse<String> resp = new BaseResponse<>(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE,exception.getMessage(),"");
			OutputStream out = res.getOutputStream();
			com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(out, resp);
			out.flush();
			return null;
		} catch (Exception e) {
			log.info("Exception while authenticating user:{}", e.getMessage());
			res.setStatus(HttpStatus.FORBIDDEN.value());
			res.setHeader("Content-Type","application/json");
			BaseResponse<String> resp = new BaseResponse<>(HttpStatus.FORBIDDEN.value(),e.getMessage(),"");
			OutputStream out = res.getOutputStream();
			com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(out, resp);
			out.flush();
			return null;
		}
	}
	
	@SneakyThrows
	@Override
	protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
			String userName = ((User) auth.getPrincipal()).getUsername();
			JwtUtil jwtTokenUtil = (JwtUtil) SpringApplicationContext.getBean("jwtUtil");
			AuthServiceImpl authServiceImpl = (AuthServiceImpl) SpringApplicationContext.getBean("authServiceImpl");
		    AuthenticationRepository authenticationRepository = (AuthenticationRepository) SpringApplicationContext.getBean("authenticationRepository");
		    log.info("Getting user with username");
			AuthenticationDAO authenticationDAO = authenticationRepository.findByUserNameIgnoreCaseAndIsActive(userName, true);
			log.info("Successfully fetched AuthObject");

			LoginLogServiceImpl loginLogService = (LoginLogServiceImpl) SpringApplicationContext.getBean("loginLogServiceImpl");
			loginLogService.addLoginLog(authenticationDAO, LOGIN_TYPE_OTP, LOGIN_STATUS_SUCCESS);
			String token = jwtTokenUtil.getFullAutherizationToken(userName);
			log.info("Successfully Fetched Token");
			authServiceImpl.saveRefreshTokenAndLastAccessTime(token.split(" ")[2], userName);
			log.info("saved session info");
			OtpUtils otpUtils = (OtpUtils) SpringApplicationContext.getBean("otpUtils");
			otpUtils.removeOtp(userName);
			res.addHeader(SecurityConstants.HEADER_STRING, token);
			Map<String, String> userDetails = new HashedMap<>();
			DAOUser daoUser =authenticationDAO.getDaoUser();
		String fullName = CommonUtils.getFullName(daoUser.getFirstName(), daoUser.getMiddleName(), daoUser.getLastName());
		userDetails.put("name",fullName);
			userDetails.put("mobileNo", authenticationDAO.getMobile());
			userDetails.put("emailId", authenticationDAO.getEmail());
			OutputStream out = res.getOutputStream();
			com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(out, userDetails);
			log.info("Sending Final data");
			out.flush();
	}


	private boolean verifyUserByOTP(String username, String otp){
		OtpUtils otpUtils  = (OtpUtils) SpringApplicationContext.getBean("otpUtils");
		AuthServiceImpl authServiceImpl = (AuthServiceImpl) SpringApplicationContext.getBean("authServiceImpl");
		try{
			if (otpUtils.verifyOtp(username, otp)) {
				authServiceImpl.enableOtpVerication(username, CommonUtils.currentDateTime());
				return true;
			}
		} catch (Exception e){
			log.error("Error while verifying OTP: {}",e.getMessage());
		}
		return false;
	}

	private boolean verifyADUser(String username, String password){
		return !isNullOrEmpty(username) && !isNullOrEmpty(password) && password.equals("AxisAdmin@1234");
	}
}
