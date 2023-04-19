package com.axisbank.transit.authentication.util;

import com.axisbank.transit.authentication.exceptions.IdleTimeoutException;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.core.shared.utils.RedisClient;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

	private String secret;
	private int jwtExpirationInSec;
	private int refreshExpirationDateInSec;
	private int idleSessionTimeOut;

	@Value("${app.jwt.secret}")
	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Value("${app.jwt.expirationDateInSec}")
	public void setJwtExpirationInSec(int jwtExpirationInSec) {
		this.jwtExpirationInSec = jwtExpirationInSec;
	}
	
	@Value("${app.jwt.refreshExpirationDateInSec}")
	public void setRefreshExpirationDateInSec(int refreshExpirationDateInSec) {
		this.refreshExpirationDateInSec = refreshExpirationDateInSec;
	}

	@Value("${app.jwt.IdleSessionTimeOut}")
	public void setIdleSessionTimeOut(int idleSessionTimeOut) {
		this.idleSessionTimeOut = idleSessionTimeOut;
	}

	@Autowired
    RedisClient redisClient;

	public String generateToken(User userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("isAccessToken", true);
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		long currentTimeInMillis = CommonUtils.getCurrentTimeMillis();
		String token = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(currentTimeInMillis))
				.setExpiration(new Date(currentTimeInMillis + jwtExpirationInSec*1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
			long exp_time = CommonUtils.getCurrentTimeSec()+ idleSessionTimeOut;
			String redisKey = subject+":"+token;
			redisClient.deletePattern(subject+":*");
			redisClient.setValue(redisKey, String.valueOf(exp_time), idleSessionTimeOut);
		return token;

	}

	public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
		long currentTimeInMillis = CommonUtils.getCurrentTimeMillis();
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(currentTimeInMillis))
				.setExpiration(new Date(currentTimeInMillis + refreshExpirationDateInSec*1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();

	}

	public boolean validateToken(String authToken) {
		try {
			String userName = "";
			try{
				Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
				userName = claims.getBody().getSubject();
			} catch (ExpiredJwtException ex){
				userName =ex.getClaims().getSubject();
			}
			String redisKey= userName+":"+authToken;
			String res = redisClient.getValue(redisKey);
			if (res==null){
				throw new IdleTimeoutException("Session TimeOut");
			}
			Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
			long exp_time = CommonUtils.getCurrentTimeSec()+ idleSessionTimeOut;
			redisClient.setValue(redisKey, String.valueOf(exp_time), idleSessionTimeOut);
			return true;
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
		}
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		return claims.getSubject();

	}

	/**
	 * generate full jwt token (access+refresh) in autherization header
	 * @param username
	 * @return
	 */
	public String getFullAutherizationToken(String username) {
		Map<String, Object> accessClaims = new HashMap<>();
		accessClaims.put("isAccessToken", true);
		String accessToken = doGenerateToken(accessClaims, username);
		Map<String, Object> refreshClaims = new HashMap<>();
		refreshClaims.put("isRefreshToken", true);
		String refreshToken = doGenerateRefreshToken(refreshClaims, username);
		return "Bearer "+accessToken+ " "+refreshToken;
	}
}
