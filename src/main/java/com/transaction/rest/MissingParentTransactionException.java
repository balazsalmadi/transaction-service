package com.transaction.rest;

public class MissingParentTransactionException extends RuntimeException {
    public MissingParentTransactionException( String message ) {
        super( message );
    }
}
