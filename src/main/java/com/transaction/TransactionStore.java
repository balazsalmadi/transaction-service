package com.transaction;

import com.transaction.rest.MissingParentTransactionException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is represents the transaction database.
 */
@Repository
public class TransactionStore {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ConcurrentHashMap<Long, Transaction> transactions = new ConcurrentHashMap<>();
    private Map<String, List<Long>> typeViews = new HashMap<>();

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
        if ( isNewTransaction ) {
            updateTypeView( transactionId, transaction.type() );
        }
        lock.writeLock().unlock();
        return isNewTransaction;
    }

    private void updateTypeView( long transactionId, String transactionType ) {
        List<Long> typeView = Optional.ofNullable( typeViews.get( transactionType ) ).orElse( new ArrayList<Long>() );
        typeView.add( transactionId );
        typeViews.put( transactionType, typeView );
    }

    private void assureParentExists( Transaction transaction ) {
        Long parentId = transaction.parentId();
        if ( parentId != null ) {
            Optional.ofNullable( retrieve( parentId ) )
                    .orElseThrow( () -> new MissingParentTransactionException( "Parent transaction '" + parentId + "' is missing!" ) );
        }
    }

    /**
     * Retrives the transaction associated with the given transaction id.
     *
     * @param transactionId expected transaction's id
     * @return The {@link Transaction} instance that is associated with the give transaction id,
     * or {@code null} if no transaction is associated with the given id.
     */
    public Transaction retrieve( long transactionId ) {
        Transaction transaction;
        lock.readLock().lock();
        transaction = transactions.get( transactionId );
        lock.readLock().unlock();
        return transaction;
    }

    /**
     * Retrieves a collection of transaction ids those belong to a given type.
     *
     * @param type type to retrieve by
     * @return list of transaction ids those belong to the given type, or empty list if no transaction belongs to the type.
     */
    public List<Long> retrieveByType( String type ) {
        List<Long> typeView;
        lock.readLock().lock();
        typeView = Optional.ofNullable( typeViews.get( type ) ).orElse( new ArrayList<>() );
        lock.readLock().unlock();
        return typeView;
    }
}
