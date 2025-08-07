package com.bankingsystem.transaction.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bankingsystem.transaction.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bankingsystem.transaction.helper.ApiResponse;
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

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleTransactionNotFound(TransactionNotFoundException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTransaction(InvalidTransactionException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Invalid transaction: {}", ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Method not allowed: {}", ex.getMethod());
        }
        return METHOD_NOT_ALLOWED_RESPONSE;
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<String> handleTransferException(TransferException ex) {
        log.error("Transfer exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Transfer failed: " + ex.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJson(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON request format";
        Map<String, String> details = new HashMap<>();

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



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed for one or more fields")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Account not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }



    @ExceptionHandler(NotificationClientException.class)
    public ResponseEntity<String> handleNotificationClient(NotificationClientException ex) {
        log.error("Notification client failed: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Failed to send notification: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
    }

}
