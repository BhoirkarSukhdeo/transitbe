package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

import java.util.HashMap;
import java.util.Map;

public class Body {

    public Map<String, EncryptedRequest> body = new HashMap<>();

    public Map<String, EncryptedRequest> getBody() {
        return body;
    }

    public void setBody(Map<String, EncryptedRequest> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Body{" +
                "body=" + body +
                '}';
    }
}
