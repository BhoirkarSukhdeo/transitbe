package com.axisbank.transit.transitCardAPI.exceptions;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException(String message) {
        super(message);
    }
}
