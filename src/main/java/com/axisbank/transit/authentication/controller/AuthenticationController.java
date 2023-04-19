package com.axisbank.transit.authentication.controller;

import com.axisbank.transit.authentication.constants.AuthenticationConstants;
import com.axisbank.transit.authentication.constants.GetOtpType;
import com.axisbank.transit.authentication.exceptions.InvalidMpinException;
import com.axisbank.transit.authentication.exceptions.InvalidRefreshTokenException;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.exceptions.MpinValidationException;
import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.model.DTO.AuthDTO;
import com.axisbank.transit.authentication.model.DTO.GetOtpDTO;
import com.axisbank.transit.authentication.model.DTO.OtkDTO;
import com.axisbank.transit.authentication.model.DTO.SetMpinDTO;
import com.axisbank.transit.authentication.model.request.OtpRequest;
import com.axisbank.transit.authentication.model.request.RefreshTokenRequest;
import com.axisbank.transit.authentication.model.request.RefreshTokenWithMpinRequest;
import com.axisbank.transit.authentication.model.response.RefreshTokenResponse;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.authentication.service.AuthService;
import com.axisbank.transit.authentication.service.CustomUserDetailsService;
import com.axisbank.transit.authentication.service.LoginLogService;
import com.axisbank.transit.authentication.util.JwtUtil;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.OtpUtils;
import com.axisbank.transit.core.shared.utils.SendSmsUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Objects;

import static com.axisbank.transit.authentication.constants.AuthenticationConstants.*;
import static com.axisbank.transit.authentication.constants.CommonConstants.*;
import static com.axisbank.transit.authentication.constants.GetOtpType.login;
import static com.axisbank.transit.authentication.constants.LoginLogConstants.*;
import static com.axisbank.transit.core.shared.constants.ApiConstants.*;
import static com.axisbank.transit.core.shared.constants.TransitAPIConstants.*;
import static com.axisbank.transit.core.shared.constants.UtilsConstants.COUNTRY_CODE_WITHOUT_PLUS;

@Slf4j
@RestController
@RequestMapping(ApiConstants.BASE_URI)
public class AuthenticationController {

	@Value("${app.otp.expiration}")
	private int expiration;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private OtpUtils otpUtils;

	@Autowired
	private SendSmsUtil sendSmsUtil;

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private LoginLogService loginLogService;

	@RequestMapping(value = REGISTER_URI, method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<GetOtpDTO>> saveUser(@Valid @RequestBody AuthDTO user) throws Exception {
		if(user.getMobile()!=null && !user.getMobile().equals("")){
			// add 91 prefix for mobile numbers
			user.setMobile(COUNTRY_CODE_WITHOUT_PLUS+user.getMobile());
		}
		AuthenticationDAO savedUser = authService.saveUser(user);
		if (savedUser != null) {
			String mobileNumber = savedUser.getMobile();
			String otpValue = otpUtils.generateOtp(mobileNumber);
			String currentDateTime = CommonUtils.currentDateTime("dd-MM-yy; HH:mm:ss");
			String otpExpiryDateTime = CommonUtils.addSecondsToTime("dd-MM-yy; HH:mm:ss", currentDateTime, expiration);
			String registeringOtpMessage = MessageFormat.format(REGISTERING_OTP_MESSAGE,otpValue, otpExpiryDateTime);
			sendSmsUtil.sendSms(registeringOtpMessage,mobileNumber);
			authService.setUserRole(savedUser);
			GetOtpDTO getOtpDTO = new GetOtpDTO(REGISTRATION_SUCCESS_MESSAGE, otpExpiryDateTime);
			log.info("Verifying User: {}, OTP:{}",CommonUtils.maskString(mobileNumber,0,mobileNumber.length()-4,'*'),
					CommonUtils.maskString(otpValue,0,otpValue.length()-2,'*'));
			return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, getOtpDTO);
		}
		return BaseResponseType.errorResponse(API_FAIL_CODE, REGISTRATION_FAILURE_MESSAGE);

	}
	
	@RequestMapping(value = REFRESH_TOKEN_URI, method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshtoken(@RequestBody RefreshTokenRequest request) throws Exception {
		String refreshToken = request.getRefreshToken();
		try {
			String userName = jwtUtil.getUsernameFromToken(refreshToken);
			authService.checkRefreshToken(refreshToken, userName);
			User userdetails = userDetailsService.loadUserByUsername(userName);
			String accessToken = jwtUtil.generateToken(userdetails);
			return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, new RefreshTokenResponse(accessToken));
		} catch (ExpiredJwtException exception) {
			return BaseResponseType.forbiddenResponse(API_TOKEN_INVALID, AuthenticationConstants.EXPIRED_REFRESH_TOKEN_MESSAGE);
		} catch (InvalidRefreshTokenException exception) {
			return BaseResponseType.forbiddenResponse(API_TOKEN_INVALID, exception.getMessage());
		} catch (MpinBlockedUserException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE, exception.getMessage());
		} catch (InvalidMpinException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_INVALID, exception.getMessage());
		} catch (UsernameNotFoundException exception) {
			return BaseResponseType.forbiddenResponse(TransitAPIConstants.API_TOKEN_INVALID, USER_NOT_FOUND);
		} catch (Exception exception) {
			log.error("Exception:{}", exception.getMessage());
			return BaseResponseType.errorResponse(API_FAIL_CODE, AuthenticationConstants.INTERNAL_SERVER_ERROR_MESSAGE);
		}
	}

	@RequestMapping(value = REFRESH_TOKEN_URI+MPIN, method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshtokenWithMpin(@RequestBody RefreshTokenWithMpinRequest request) throws Exception {
		String refreshToken = request.getRefreshToken();
		String userName;
		String mobile = null;
		String registeringOtpMessage=null;
		int attemptCount;
		try{
			userName = jwtUtil.getUsernameFromToken(refreshToken);
		} catch (ExpiredJwtException exception) {
			userName = exception.getClaims().getSubject();
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			log.error("Jwt refresh token Expired: {}", exception.getMessage());
			return BaseResponseType.forbiddenResponse(API_TOKEN_EXPIRE, AuthenticationConstants.EXPIRED_REFRESH_TOKEN_MESSAGE);
		} catch (Exception exception) {
			log.error("Exception:{}", exception.getMessage());
			return BaseResponseType.errorResponse(API_FAIL_CODE, AuthenticationConstants.INTERNAL_SERVER_ERROR_MESSAGE);
		}
		try {
			authService.checkRefreshToken(refreshToken, userName);
			authService.checkMpin(userName, request.getMpin());
			User userdetails = userDetailsService.loadUserByUsername(userName);
			String accessToken = jwtUtil.generateToken(userdetails);
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_SUCCESS);
			return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, new RefreshTokenResponse(accessToken));
		} catch (InvalidRefreshTokenException exception) {
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			return BaseResponseType.forbiddenResponse(API_TOKEN_INVALID, exception.getMessage());
		} catch (MpinBlockedUserException exception) {
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE, exception.getMessage());
		} catch (InvalidMpinException exception) {

			AuthenticationDAO authenticationDAO  =	authService.getMobileNumber(userName);
			mobile = authenticationDAO.getMobile();
			attemptCount=authenticationDAO.getSessionDAO().getUserAttempts();
			attemptCount = 5 - attemptCount;
			registeringOtpMessage = MessageFormat.format(INVALID_MPIN_OTP_MESSAGE,attemptCount);

			log.error(registeringOtpMessage);

			if(!Objects.isNull(mobile))
				sendSmsUtil.sendSms(registeringOtpMessage,mobile);

			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_INVALID, exception.getMessage());
		} catch (UsernameNotFoundException exception) {
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			return BaseResponseType.forbiddenResponse(TransitAPIConstants.API_TOKEN_INVALID, USER_NOT_FOUND);
		} catch (Exception exception) {
			loginLogService.addLoginLog(userName, LOGIN_TYPE_MPIN, LOGIN_STATUS_FAILED);
			log.error("Exception:{}", exception.getMessage());
			return BaseResponseType.errorResponse(API_FAIL_CODE, AuthenticationConstants.INTERNAL_SERVER_ERROR_MESSAGE);
		}
	}

	@RequestMapping(value = ApiConstants.GET_OTP, method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<GetOtpDTO>> getOTP(@RequestBody OtpRequest request) throws Exception {
		if(request.getMobile()!=null && !request.getMobile().equals("")){
			// add 91 prefix for mobile numbers
			request.setMobile(COUNTRY_CODE_WITHOUT_PLUS+request.getMobile());
		}
		AuthenticationDAO authenticationDAO = null;
		try {
			userDetailsService.loadUserByUsername(request.getMobile());
			authenticationDAO = authenticationRepository.findByMobileAndIsActive(request.getMobile(), true);
		} catch (MpinBlockedUserException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE, exception.getMessage());
		} catch (Exception exception) {
			return BaseResponseType.errorResponse(API_FAIL_CODE, exception.getMessage());
		}

		String otpValue = otpUtils.generateOtp(request.getMobile());
		String currentDateTime = CommonUtils.currentDateTime("dd-MM-yy; HH:mm:ss");
		String otpExpiryDateTime = CommonUtils.addSecondsToTime("dd-MM-yy; HH:mm:ss", currentDateTime, expiration);
		String otpMessage = "";
		GetOtpType otpType = request.getGetOtpType()!=null?request.getGetOtpType():login;
		switch (otpType) {
			case login:
				if (authenticationDAO.getSessionDAO() != null) {
					if (authenticationDAO.getSessionDAO().getRefreshToken() !=null) {
						otpMessage = MessageFormat.format(NEW_DEVICE_LOGIN_OTP_MESSAGE, otpValue, otpExpiryDateTime);
					} else {
						otpMessage = MessageFormat.format(OTP_MESSAGE, otpValue, otpExpiryDateTime);
					}
				} else {
					otpMessage = MessageFormat.format(OTP_MESSAGE, otpValue, otpExpiryDateTime);
				}
				break;
			case resetMPIN:
				otpMessage = MessageFormat.format(RESET_MPIN_OTP_MESSAGE, otpValue, otpExpiryDateTime);
				break;
			case resend:
				otpMessage = MessageFormat.format(OTP_MESSAGE, otpValue, otpExpiryDateTime);
				break;
			default:
				otpMessage = MessageFormat.format(OTP_MESSAGE, otpValue, otpExpiryDateTime);
				break;
		}

		sendSmsUtil.sendSms(otpMessage,request.getMobile());
		GetOtpDTO getOtpDTO = new GetOtpDTO(REGISTRATION_SUCCESS_MESSAGE, otpExpiryDateTime);
		log.info("Verifying User: {}, OTP:{}",CommonUtils.maskString(request.getMobile(),0,request.getMobile().length()-4,'*'),
				CommonUtils.maskString(otpValue,0,otpValue.length()-2,'*'));
		return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, getOtpDTO);
	}

	@RequestMapping(value = "/delete/{mobileNumber}", method = RequestMethod.GET)
	public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable String mobileNumber) throws Exception {
		authService.deleteUser(mobileNumber);
		return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, "Successfully deleted");
	}

	@PostMapping("/mpin")
	public ResponseEntity<BaseResponse<String>> setMpin(@RequestBody SetMpinDTO setMpinDTO) throws Exception{
		try {
			AuthenticationDAO authenticationDAO = authService.setMpin(setMpinDTO);
			if(!Objects.isNull(authenticationDAO)){
					log.info(RESET_MPIN_MESSAGE);
					sendSmsUtil.sendSms(RESET_MPIN_MESSAGE,authenticationDAO.getMobile());
			}
		} catch (MpinValidationException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_VALIDATION_ERROR_CODE, exception.getMessage());
		} catch (MpinBlockedUserException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE, exception.getMessage());
		} catch (InvalidMpinException exception) {
			return BaseResponseType.errorResponse(TransitAPIConstants.API_MPIN_INVALID, exception.getMessage());
		} catch (Exception exception) {
			return BaseResponseType.errorResponse(API_FAIL_CODE, exception.getMessage());
		}

		return BaseResponseType.successfulResponse(TransitAPIConstants.API_SUCCESS_CODE, SET_MPIN_SUCCESS_MESSAGE);
	}

	@PostMapping("/confirmCifId")
	public ResponseEntity<BaseResponse<String>> confirmCifId(@RequestBody AuthDTO authDTO) throws Exception {
		log.info("Request receive to validate cifId and confirm");
			return BaseResponseType.successfulResponse(API_SUCCESS_CODE,authService.confirmCifId(authDTO));
	}

	@PostMapping("/otk")
	public ResponseEntity<BaseResponse<String>> getOTK(@RequestBody OtkDTO otkDTO) throws Exception {
		String userName = jwtUtil.getUsernameFromToken(otkDTO.getRefreshToken());
		return BaseResponseType.successfulResponse(API_SUCCESS_CODE,
				authService.generateOTK(otkDTO.getRefreshToken(),userName));
	}
}
