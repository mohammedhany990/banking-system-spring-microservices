package com.bankingsystem.transaction.exception;

public class TransferException extends RuntimeException {

    public TransferException() {
        super();
    }

    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferException(Throwable cause) {
        super(cause);
    }
}
