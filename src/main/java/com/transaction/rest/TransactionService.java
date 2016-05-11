package com.transaction.rest;


import com.transaction.Transaction;
import com.transaction.TransactionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/transactionservice" )
public class TransactionService {

    @Autowired
    private TransactionStore transactionStore;

    @RequestMapping( method = RequestMethod.PUT, path = "/transaction/{transactionId}" )
    @ResponseStatus( HttpStatus.CREATED )
    public void createTransaction( @PathVariable long transactionId, @RequestBody Transaction transaction ) {
        if ( !transactionStore.addTransaction( transactionId, transaction ) ) {
            throw new TransactionAlreadyExistsException( "Transaction is already exists with id: '" + transactionId + "'!" );
        }
    }
}
