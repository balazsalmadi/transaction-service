package com.transaction;

import com.transaction.rest.MissingParentTransactionException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is represents the transaction database.
 */
@Repository
public class TransactionStore {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ConcurrentHashMap<Long, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * Add new transaction to the transaction store. It assure the parent transaction is exists.
     *
     * @param transactionId Unique transaction id
     * @param transaction   transaction to store
     * @return {@code true} if the transaction id is not present in the store, otherwise {@code false}
     */
    public boolean addTransaction( long transactionId, Transaction transaction ) {
        lock.writeLock().lock();
        assureParentExists( transaction );
        boolean isNewTransaction = transactions.putIfAbsent( transactionId, transaction ) == null;
        lock.writeLock().unlock();
        return isNewTransaction;
    }

    private void assureParentExists( Transaction transaction ) {
        Long parentId = transaction.parentId();
        if ( parentId != null ) {
            Optional.ofNullable( transactions.get( parentId ) )
                    .orElseThrow( () -> new MissingParentTransactionException( "Parent transaction '" + parentId + "' is missing!" ) );
        }
    }
}
