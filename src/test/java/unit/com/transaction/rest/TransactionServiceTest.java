package unit.com.transaction.rest;

import com.transaction.Transaction;
import com.transaction.rest.TransactionService;
import component.com.transaction.TransactionStringFactory;
import org.junit.Test;

public class TransactionServiceTest {

    @Test
    public void transactionCanBeCreated() throws Exception {
        TransactionService transactionService = new TransactionService();
        transactionService.createTransaction( 10, new Transaction( 10, "a" ) );
    }

    @Test
    public void transactionAlreadyExists() throws Exception {
        TransactionService transactionService = new TransactionService();
        transactionService.createTransaction( 10, new Transaction( 10, "a" ) );
        transactionService.createTransaction( 10, new Transaction( 10, "a" ) );
    }

    @Test
    public void name() throws Exception {
        System.out.println(TransactionStringFactory.transactionAsString( new Transaction( 10, "cars" ) ));

    }
}
