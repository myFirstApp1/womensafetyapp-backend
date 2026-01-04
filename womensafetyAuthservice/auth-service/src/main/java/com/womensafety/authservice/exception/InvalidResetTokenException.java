package com.womensafety.authservice.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException(String msg) {
        super(msg);
    }
}