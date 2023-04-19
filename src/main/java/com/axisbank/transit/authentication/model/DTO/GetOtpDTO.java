package com.axisbank.transit.authentication.model.DTO;

public class GetOtpDTO {
    private String message;
    private String expiryTime;

    public GetOtpDTO() {
    }

    public GetOtpDTO(String message, String expiryTime) {
        this.message = message;
        this.expiryTime = expiryTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
}
