package com.womensafety.authservice.exception;

import com.womensafety.authservice.advice.ResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleHttpClientError(HttpClientErrorException ex, WebRequest request) {
        log.error("Client error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error("Something failed"));

    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleHttpServerError(HttpServerErrorException ex, WebRequest request) {
        log.error("Server error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error("Something failed"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.error("Validation failed: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error("Something failed"));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleUnreadableMessage(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Malformed JSON request: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error("Malformed JSON request"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        request.setAttribute("SKIP_WRAP", true);  // Tell the advice to skip wrapping

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.putIfAbsent(error.getField(), error.getField() + ": " + error.getDefaultMessage());
        });

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timestamp", LocalDateTime.now());
        data.put("status", HttpStatus.BAD_REQUEST.value());
        data.put("errors", fieldErrors.values());

        return ResponseEntity.badRequest().body(
                ResponseWrapper.error("Validation Failed", data)
        );
    }


    @ExceptionHandler(java.net.SocketTimeoutException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleSocketTimeout(SocketTimeoutException ex, WebRequest request) {
        log.error("Socket timeout: ", ex);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ResponseWrapper.error("Request timed out"));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error("An unexpected error occurred"));
    }

}
