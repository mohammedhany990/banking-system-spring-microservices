package com.bankingsystem.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }

    public CustomerNotFoundException(String username, String message) {
        super("Customer not found with username: " + username + ". " + message);
    }
    
}
