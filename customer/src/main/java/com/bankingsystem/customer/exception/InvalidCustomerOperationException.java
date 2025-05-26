package com.bankingsystem.customer.exception;

public class InvalidCustomerOperationException extends RuntimeException {
    public InvalidCustomerOperationException(String message) {
        super(message);
    }
}