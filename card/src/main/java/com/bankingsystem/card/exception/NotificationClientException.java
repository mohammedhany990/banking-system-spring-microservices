package com.bankingsystem.card.exception;

public class NotificationClientException extends RuntimeException {
    public NotificationClientException(String message) {
        super(message);
    }

    public NotificationClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
