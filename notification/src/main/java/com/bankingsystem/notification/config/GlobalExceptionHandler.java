package com.bankingsystem.notification.config;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bankingsystem.notification.exception.InvalidNotificationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidNotificationException.class)
    public ResponseEntity<String> handleInvalidNotificationException(InvalidNotificationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }
   

}
