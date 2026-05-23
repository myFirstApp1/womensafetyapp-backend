package com.womensafety.sosservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================
    // VALIDATION ERRORS
    // =========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity.badRequest().body(errors);
    }

    // =========================================
    // ILLEGAL ARGUMENT
    // =========================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // =========================================
    // RUNTIME
    // =========================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(
            RuntimeException ex) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    // =========================================
    // GENERIC
    // =========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(
            Exception ex) {

        log.error("UNHANDLED_EXCEPTION", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong"
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockException(
            ObjectOptimisticLockingFailureException ex
    ) {

        log.error("OPTIMISTIC_LOCK_FAILURE", ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Concurrent update detected. Please retry.");
    }

    // =========================================
    // HELPER
    // =========================================

    private ResponseEntity<?> buildError(
            HttpStatus status,
            String message) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", message);

        return ResponseEntity.status(status).body(body);
    }
}