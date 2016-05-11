package com.transaction.rest;

public class TransactionAlreadyExistsException extends RuntimeException {

    public TransactionAlreadyExistsException( String message ) {
        super( message );
    }
}
