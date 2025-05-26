package com.bankingsystem.bankaccount.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bankingsystem.bankaccount.exception.BankAccountAlreadyExistsException;
import com.bankingsystem.bankaccount.exception.BankAccountNotFoundException;
import com.bankingsystem.bankaccount.exception.InvalidBankAccountOperationException;
import com.bankingsystem.bankaccount.helper.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final ResponseEntity<ApiResponse<?>> METHOD_NOT_ALLOWED_RESPONSE = new ResponseEntity<>(
            new ApiResponse<>(false, "HTTP method not allowed for this endpoint.", null),
            HttpStatus.METHOD_NOT_ALLOWED);

    private static final ResponseEntity<ApiResponse<?>> INTERNAL_ERROR_RESPONSE = new ResponseEntity<>(
            new ApiResponse<>(false, "An unexpected error occurred.", null),
            HttpStatus.INTERNAL_SERVER_ERROR);

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleBankAccountNotFound(BankAccountNotFoundException ex) {
        log.warn("Bank account not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<String>builder().success(false).message(ex.getMessage()).build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not allowed: {}", ex.getMethod());
        return METHOD_NOT_ALLOWED_RESPONSE;
    }

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse<Void>> handleConflict(Exception ex) {
        log.warn("Conflict detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJson(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON request format";
        Map<String, String> details = new HashMap<>();

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormat) {
            String fieldPath = invalidFormat.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            message = "Invalid data format in request";
            details.put("field", fieldPath);
            details.put("expectedType", invalidFormat.getTargetType().getSimpleName());
            details.put("receivedValue", String.valueOf(invalidFormat.getValue()));
            details.put("message", "Field '" + fieldPath + "' expects "
                    + invalidFormat.getTargetType().getSimpleName() + " but received '"
                    + invalidFormat.getValue() + "'");

        } else if (cause instanceof MismatchedInputException mismatchedInput) {
            if (!mismatchedInput.getPath().isEmpty()) {
                String fieldPath = mismatchedInput.getPath().stream()
                        .map(ref -> ref.getFieldName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("."));

                message = "Invalid request structure";
                details.put("field", fieldPath);
                details.put("message", mismatchedInput.getOriginalMessage());
            } else {
                message = mismatchedInput.getOriginalMessage();
            }
        } else {
            details.put("message", "The request contains malformed JSON syntax");
        }

        log.warn("Invalid JSON request: {} - Details: {}", message, details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, details.isEmpty() ? null : details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllUnhandledExceptions(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return INTERNAL_ERROR_RESPONSE;
    }

    @ExceptionHandler(BankAccountAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExists(BankAccountAlreadyExistsException ex) {
        log.warn("Account already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }

    @ExceptionHandler(InvalidBankAccountOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOperation(InvalidBankAccountOperationException ex) {
        log.warn("Invalid operation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }
}
