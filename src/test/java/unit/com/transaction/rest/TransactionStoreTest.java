package unit.com.transaction.rest;

import com.transaction.Transaction;
import com.transaction.TransactionStore;
import com.transaction.rest.MissingParentTransactionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.transaction.Transaction.childTransaction;
import static com.transaction.Transaction.rootTransaction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.rules.ExpectedException.none;
import static unit.com.transaction.TransactionMatcher.anyTransaction;

public class TransactionStoreTest {

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void rootTransactionCabBeAdded() throws Exception {
        TransactionStore transactionStore = new TransactionStore();

        boolean cars = transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );

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
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );

        boolean cars = transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );

        assertThat( "Add transaction returns 'false', if transaction id already added.", cars, is( false ) );
    }

    @Test
    public void childTransactionCannotBeAddedIfParentIsMissing() throws Exception {
        expectedException.expect( MissingParentTransactionException.class );
        expectedException.expectMessage( "Parent transaction '10' is missing!" );
        TransactionStore transactionStore = new TransactionStore();

        transactionStore.addTransaction( 11, childTransaction( 5000, "cars", 10L ) );
    }

    @Test
    public void rootTransactionRetrieve() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );

        Transaction transaction = transactionStore.retrieve( 10 );

        assertThat( transaction, anyTransaction().whichHasAmount( 5000 )
                                                 .whichHasType( "cars" ) );
    }

    @Test
    public void childTransactionRetrieve() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );
        transactionStore.addTransaction( 11, childTransaction( 15000, "shopping", 10L ) );

        Transaction transaction = transactionStore.retrieve( 11 );

        assertThat( transaction, anyTransaction().whichHasAmount( 15000 )
                                                 .whichHasType( "shopping" )
                                                 .whichHasParentId( 10 ) );
    }

    @Test
    public void retrieveTransactionByType() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );

        List<Long> transactionIds = transactionStore.retrieveByType( "cars" );

        assertThat( transactionIds, containsInAnyOrder( 10L ) );
    }

    @Test
    public void retrieveTransactionsByType() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );
        transactionStore.addTransaction( 11, rootTransaction( 15000, "cars" ) );

        List<Long> transactionIds = transactionStore.retrieveByType( "cars" );

        assertThat( transactionIds, containsInAnyOrder( 10L, 11L ) );
    }

    @Test
    public void retrieveByTypeFiltersOtherTypes() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );
        transactionStore.addTransaction( 11, rootTransaction( 6000, "shopping" ) );
        transactionStore.addTransaction( 11, rootTransaction( 7000, "tax" ) );
        transactionStore.addTransaction( 13, rootTransaction( 8000, "cars" ) );

        List<Long> transactionIds = transactionStore.retrieveByType( "cars" );

        assertThat( transactionIds, containsInAnyOrder( 10L, 13L ) );
    }

    @Test
    public void retrieveByTypeReturnsEmptyListIfNoTransactionsBelongsToTheGivenType() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 5000, "cars" ) );
        transactionStore.addTransaction( 11, rootTransaction( 6000, "shopping" ) );
        transactionStore.addTransaction( 11, rootTransaction( 7000, "tax" ) );
        transactionStore.addTransaction( 13, rootTransaction( 8000, "cars" ) );

        List<Long> transactionIds = transactionStore.retrieveByType( "not-existing-type" );

        assertThat( transactionIds, emptyCollectionOf( Long.class ) );
    }

    @Test
    public void sumOfChainWithTwoElements() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 1000, "cars" ) );
        transactionStore.addTransaction( 11, childTransaction( 2000, "shopping", 10L ) );

        double sum = transactionStore.retrieveSumOfChain( 10 );

        assertThat( sum, is( 3000. ) );
    }

    @Test
    public void sumOfChainWithFiveElements() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 1000, "cars" ) );
        transactionStore.addTransaction( 11, childTransaction( 2000, "shopping", 10L ) );
        transactionStore.addTransaction( 12, childTransaction( 3000, "shopping", 11L ) );
        transactionStore.addTransaction( 13, childTransaction( 4000, "shopping", 12L ) );
        transactionStore.addTransaction( 14, childTransaction( 5000, "shopping", 13L ) );

        double sum = transactionStore.retrieveSumOfChain( 10 );

        assertThat( sum, is( 15000. ) );
    }

    @Test
    public void sumOfChainWithForkUnderElement() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 1000, "cars" ) );
        transactionStore.addTransaction( 11, childTransaction( 2000, "shopping", 10L ) );
        transactionStore.addTransaction( 12, childTransaction( 3000, "shopping", 11L ) );
        transactionStore.addTransaction( 13, childTransaction( 4000, "shopping", 10L ) );
        transactionStore.addTransaction( 14, childTransaction( 5000, "shopping", 13L ) );

        double sum = transactionStore.retrieveSumOfChain( 10 );

        assertThat( sum, is( 15000. ) );
    }

    @Test
    public void sumOfChainNotIncludesOtherTransactionsAmount() throws Exception {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.addTransaction( 10, rootTransaction( 1000, "cars" ) );
        transactionStore.addTransaction( 11, rootTransaction( 2000, "shopping" ) );
        transactionStore.addTransaction( 12, childTransaction( 3000, "shopping", 10L ) );

        double sum = transactionStore.retrieveSumOfChain( 10 );

        assertThat( sum, is( 4000. ) );
    }
}
