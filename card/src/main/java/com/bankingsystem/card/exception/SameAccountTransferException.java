package com.bankingsystem.card.exception;

public class SameAccountTransferException extends RuntimeException {
   
    public SameAccountTransferException(String message) {
        super(message);
    }
}
