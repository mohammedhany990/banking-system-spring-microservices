package com.bankingsystem.bankaccount.exception;

public class BankAccountNotFoundException extends RuntimeException {


    public BankAccountNotFoundException(String message) {
        super(message);
    }

    
}