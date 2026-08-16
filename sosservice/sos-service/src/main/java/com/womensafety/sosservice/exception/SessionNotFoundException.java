package com.womensafety.sosservice.exception;

import java.util.UUID;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(UUID userId) {
        super("Active safety session not found for user : " + userId);
    }

}
