package com.axisbank.transit.transitCardAPI.model.request.updateLimitTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Request {

    @JsonProperty("header")
    public Header header;

    @JsonProperty("body")
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

