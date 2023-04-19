package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

public class EncryptedRequest {

    public String encryptedRequest;

    public String getEncryptedRequest() {
        return encryptedRequest;
    }

    public void setEncryptedRequest(String encryptedRequest) {
        this.encryptedRequest = encryptedRequest;
    }

    @Override
    public String toString() {
        return "EncryptedRequest{" +
                "encryptedRequest='" + encryptedRequest + '\'' +
                '}';
    }
}
