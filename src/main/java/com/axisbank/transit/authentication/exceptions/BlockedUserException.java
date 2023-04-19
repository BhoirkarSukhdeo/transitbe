package com.axisbank.transit.authentication.exceptions;

public class BlockedUserException extends RuntimeException {
    public BlockedUserException(String message) {
        super(message);
    }
}
