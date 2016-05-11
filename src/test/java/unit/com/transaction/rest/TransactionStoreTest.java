package unit.com.transaction.rest;

import com.transaction.TransactionStore;
import com.transaction.rest.MissingParentTransactionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.transaction.Transaction.childTransaction;
import static com.transaction.Transaction.rootTransaction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.rules.ExpectedException.none;

public class TransactionStoreTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void rootTransactionCabBeAdded() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        boolean cars = transactionStore.addTransaction( 10L, rootTransaction( 5000, "cars" ) );
        assertThat( "Add transaction returns 'true', if root transaction is added.", cars, is( true ) );
    }

    @Test
    public void childTransactionCabBeAdded() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );
        boolean cars = transactionStore.addTransaction( 11, childTransaction( 5000, "cars", 10L ) );
        assertThat( "Add transaction returns 'true', if child transaction is added.", cars, is( true ) );
    }

    @Test
    public void transactionCannotBeAddedTwice() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10L, rootTransaction( 5000, "cars" ) );
        boolean cars = transactionStore.addTransaction( 10L, rootTransaction( 5000, "cars" ) );
        assertThat( "Add transaction returns 'false', if transaction id already added.", cars, is( false ) );
    }

    @Test
    public void childTransactionCannotBeAddedIfParentIsMissing() throws Exception {
        expectedException.expect( MissingParentTransactionException.class );
        expectedException.expectMessage( "Parent transaction '10' is missing!" );
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 11L, childTransaction( 5000, "cars", 10L ) );
    }
}
