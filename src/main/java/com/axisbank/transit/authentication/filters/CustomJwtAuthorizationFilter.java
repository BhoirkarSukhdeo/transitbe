package com.axisbank.transit.authentication.filters;

import com.axisbank.transit.SpringApplicationContext;
import com.axisbank.transit.authentication.constants.AuthenticationConstants;
import com.axisbank.transit.authentication.constants.SecurityConstants;
import com.axisbank.transit.authentication.exceptions.BlockedUserException;
import com.axisbank.transit.authentication.exceptions.IdleTimeoutException;
import com.axisbank.transit.authentication.exceptions.MpinBlockedUserException;
import com.axisbank.transit.authentication.service.CustomUserDetailsService;
import com.axisbank.transit.authentication.service.impl.AuthServiceImpl;
import com.axisbank.transit.authentication.util.JwtUtil;
import com.axisbank.transit.core.model.response.BaseResponse;
import com.axisbank.transit.core.shared.constants.TransitAPIConstants;
import com.axisbank.transit.core.shared.utils.BaseResponseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.axisbank.transit.core.shared.constants.TransitAPIConstants.*;

@Slf4j
public class CustomJwtAuthorizationFilter extends BasicAuthenticationFilter {
	public CustomJwtAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}
	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain) throws IOException, ServletException {
		try {
			String header = req.getHeader(SecurityConstants.HEADER_STRING);

			if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
				chain.doFilter(req, res);
				return;
			}
			UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			AuthServiceImpl authServiceImpl  = (AuthServiceImpl) SpringApplicationContext.getBean("authServiceImpl");
			Object principal = authentication.getPrincipal();
			String username = ((User) principal).getUsername();
			authServiceImpl.saveRefreshTokenAndLastAccessTime(null, username);

			chain.doFilter(req, res);
		} catch (IdleTimeoutException ie){
			log.info("Exception while authenticating user: {}", ie.getMessage());
			forbiddenResponse(API_TOKEN_IDLE_TIMEOUT, ie.getMessage(), res);
		}
		catch (ExpiredJwtException ex){
			log.info("Exception while authenticating user: {}", ex.getMessage());
			forbiddenResponse(API_TOKEN_EXPIRE, ex.getMessage(), res);
		}
		catch (MpinBlockedUserException exception) {
			forbiddenResponse(TransitAPIConstants.API_MPIN_BLOCKED_USER_CODE, exception.getMessage(), res);
		}
		catch (Exception e) {
			log.info("Exception while authenticating user: {}", e.getMessage());
			forbiddenResponse(API_TOKEN_INVALID, e.getMessage(), res);
		}
	}
	private void forbiddenResponse(Integer statusCode, String message, HttpServletResponse res) throws IOException{
		res.setHeader("Content-Type","application/json");
		res.setStatus(HttpStatus.FORBIDDEN.value());
		BaseResponse<String> resp = new BaseResponse<>(statusCode,message,"");
		OutputStream out = res.getOutputStream();
		com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, resp);
		out.flush();
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);

		if (header != null) {
			String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
			JwtUtil jwtTokenUtil  = (JwtUtil) SpringApplicationContext.getBean("jwtUtil");
			if (!jwtTokenUtil.validateToken(token)){
				return null;
			}
			String user = jwtTokenUtil.getUsernameFromToken(token);
			if (user != null) {
				CustomUserDetailsService userDetailsService  = (CustomUserDetailsService) SpringApplicationContext
						.getBean("customUserDetailsService");
				User userDetails = userDetailsService.loadUserByUsername(user);
				return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			}
			return null;
		}
		return null;
	}
}