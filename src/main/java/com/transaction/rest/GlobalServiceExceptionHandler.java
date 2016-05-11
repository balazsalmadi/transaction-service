package com.transaction.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalServiceExceptionHandler {

    @ResponseStatus( HttpStatus.CONFLICT )
    @ExceptionHandler( { TransactionAlreadyExistsException.class, MissingParentTransactionException.class } )
    public @ResponseBody String handleConflict( Exception ex ) {
        return ex.getMessage();
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ExceptionHandler( { TransactionNotFoundException.class } )
    public @ResponseBody String transactionNotFound( Exception ex ) {
        return ex.getMessage();
    }
}
