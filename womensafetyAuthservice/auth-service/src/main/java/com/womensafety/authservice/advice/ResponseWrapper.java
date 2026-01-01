package com.womensafety.authservice.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private T data;

    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>(LocalDateTime.now(), "SUCCESS", message, data);
    }

    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>(LocalDateTime.now(), "ERROR", message, null);
    }

    public static <T> ResponseWrapper<T> error(String message, T data) {
        return new ResponseWrapper<>(LocalDateTime.now(), "ERROR", message, data);
    }
}

