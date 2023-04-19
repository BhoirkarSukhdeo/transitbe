package com.axisbank.transit.authentication.model.request;

public class AuthenticationRequest {
	
	private String mobile;
	private String otp;
	private String username;
	private String password;
	private String cifId;
	
	
	public AuthenticationRequest(String username, String password) {
		super();
		this.mobile = username;
		this.otp = password;
	}
	
	public AuthenticationRequest()
	{
		
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCifId() {
		return cifId;
	}

	public void setCifId(String cifId) {
		this.cifId = cifId;
	}
}
