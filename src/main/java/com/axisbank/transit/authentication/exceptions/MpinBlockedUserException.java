package com.axisbank.transit.authentication.exceptions;

public class MpinBlockedUserException extends RuntimeException {
    public MpinBlockedUserException(String message) {
        super(message);
    }
}
