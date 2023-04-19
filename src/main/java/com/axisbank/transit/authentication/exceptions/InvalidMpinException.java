package com.axisbank.transit.authentication.exceptions;

public class InvalidMpinException extends RuntimeException {
    public InvalidMpinException(String message) {
        super(message);
    }
}
