package com.axisbank.transit.transitCardAPI.model.request.transitRequestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Request {

    @JsonProperty("Header")
    public Header header;

    @JsonProperty("Body")
    public Map<String, ?> body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Map<String, ?> getBody() {
        return body;
    }

    public void setBody(Map<String, ?> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}

