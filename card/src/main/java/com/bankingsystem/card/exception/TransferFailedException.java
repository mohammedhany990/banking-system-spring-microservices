package com.bankingsystem.card.exception;

public class TransferFailedException extends RuntimeException {
    public TransferFailedException() {
        super();
    }
    public TransferFailedException(String message) {
        super(message);
    }
}