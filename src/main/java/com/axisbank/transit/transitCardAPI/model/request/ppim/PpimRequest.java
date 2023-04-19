package com.axisbank.transit.transitCardAPI.model.request.ppim;

import com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate.Header;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PpimRequest {

    @JsonProperty("Body")
    public Map<String, ?> body;

    @JsonProperty("Header")
    public PpimHeader header;

    public Map<String, ?> getBody() {
        return body;
    }

    public void setBody(Map<String, ?> body) {
        this.body = body;
    }

    public PpimHeader getHeader() {
        return header;
    }

    public void setHeader(PpimHeader header) {
        this.header = header;
    }


    @Override
    public String toString() {
        return "PpimRequest{" +
                "body=" + body +
                ", header=" + header +
                '}';
    }
}
