package com.axisbank.transit.authentication.model.response;

public class RefreshTokenResponse {

	private String accessToken;
	
	public RefreshTokenResponse()
	{
		
	}

	public RefreshTokenResponse(String token) {
		super();
		this.accessToken = token;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
