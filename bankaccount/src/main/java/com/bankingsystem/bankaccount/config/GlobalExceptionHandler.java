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

import com.bankingsystem.bankaccount.exception.BankAccountNotFoundException;
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

    // 404 Not Found (custom)
    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleBankAccountNotFound(BankAccountNotFoundException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 400 Bad Request (custom)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Bad request: {}", ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // // 401 Unauthorized - Spring Security Authentication Exception
    // @ExceptionHandler(AuthenticationException.class)
    // public ResponseEntity<ApiResponse<Void>>
    // handleUnauthorized(AuthenticationException ex) {
    // if (log.isWarnEnabled()) {
    // log.warn("Unauthorized access: {}", ex.getMessage());
    // }
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    // .body(ApiResponse.<Void>builder()
    // .success(false)
    // .message("Unauthorized: " + ex.getMessage())
    // .build());
    // }

    // // 403 Forbidden - Spring Security Access Denied Exception
    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ApiResponse<Void>>
    // handleForbidden(AccessDeniedException ex) {
    // if (log.isWarnEnabled()) {
    // log.warn("Forbidden access: {}", ex.getMessage());
    // }
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ApiResponse.<Void>builder()
    // .success(false)
    // .message("Forbidden: " + ex.getMessage())
    // .build());
    // }

    // 405 Method Not Allowed (using cached response)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Method not allowed: {}", ex.getMethod());
        }
        return METHOD_NOT_ALLOWED_RESPONSE;
    }

    // 409 Conflict - Example handler for duplicate resource, customize as needed
    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse<Void>> handleConflict(Exception ex) {
        log.warn("Conflict detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // 400 Malformed JSON - detailed error handling
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJson(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON request format";
        Map<String, String> details = new HashMap<>(4);

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldPath = invalidFormatException.getPath()
                    .stream()
                    .map(ref -> ref.getFieldName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            String targetType = invalidFormatException.getTargetType().getSimpleName();
            String receivedValue = String.valueOf(invalidFormatException.getValue());

            message = "Invalid data format in request";
            details.put("field", fieldPath);
            details.put("expectedType", targetType);
            details.put("receivedValue", receivedValue);
            details.put("message",
                    "Field '" + fieldPath + "' expects " + targetType + " but received '" + receivedValue + "'");

        } else if (cause instanceof MismatchedInputException mismatchedInputException) {
            if (mismatchedInputException.getPath() != null && !mismatchedInputException.getPath().isEmpty()) {
                String fieldPath = mismatchedInputException.getPath()
                        .stream()
                        .map(ref -> ref.getFieldName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("."));

                message = "Invalid request structure";
                details.put("field", fieldPath);
                details.put("message", mismatchedInputException.getOriginalMessage());
            } else {
                message = mismatchedInputException.getOriginalMessage();
            }
        } else {
            details.put("message", "The request contains malformed JSON syntax");
        }

        if (log.isWarnEnabled()) {
            log.warn("Invalid JSON request: {} - Details: {}", message, details);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, details.isEmpty() ? null : details));
    }

    // 500 Internal Server Error - fallback handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllUnhandledExceptions(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return INTERNAL_ERROR_RESPONSE;
    }
}
