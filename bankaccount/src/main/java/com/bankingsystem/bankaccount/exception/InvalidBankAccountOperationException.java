package com.bankingsystem.bankaccount.exception;

public class InvalidBankAccountOperationException extends RuntimeException {
    public InvalidBankAccountOperationException(String message) {
        super(message);
    }
}
