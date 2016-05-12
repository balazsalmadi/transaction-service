package integration.com.transaction.rest;

import integration.com.transaction.IntegrationTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.transaction.Transaction.childTransaction;
import static com.transaction.Transaction.rootTransaction;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { IntegrationTestConfiguration.class } )
@WebAppConfiguration
@DirtiesContext( classMode = BEFORE_EACH_TEST_METHOD )
public class TransactionRestControllerEndToEndTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private TransactionRestControllerDriver transactionControllerDriver;

    @Test
    public void transactionCanBeCreated() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .resultStatusIs( HttpStatus.CREATED );
    }

    @Test
    public void transactionIsUnique() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 10, rootTransaction( 15000, "shopping" ) )
                                   .resultStatusIs( HttpStatus.CONFLICT )
                                   .resultContentIs( "Transaction is already exists with id: '10'!" );

    }

    @Test
    public void parentOfChildTransactionMustExists() throws Exception {
        transactionControllerDriver.putTransaction( 10, childTransaction( 5000, "cars", 10L ) )
                                   .resultStatusIs( HttpStatus.BAD_REQUEST )
                                   .resultContentIs( "Parent transaction '10' is missing!" );
    }

    @Test
    public void transactionChainCanBeCreated() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 11, childTransaction( 10000, "shopping", 10L ) )
                                   .resultStatusIs( HttpStatus.CREATED );
    }

    @Test
    public void rootTransactionCanBeQueried() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .getTransaction( 10 )
                                   .resultStatusIs( HttpStatus.OK )
                                   .resultContentIs( "{\"amount\":5000.0,\"type\":\"cars\"}" );
    }

    @Test
    public void childTransactionCanBeQueried() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 11, childTransaction( 15000, "shopping", 10L ) )
                                   .getTransaction( 11 )
                                   .resultStatusIs( HttpStatus.OK )
                                   .resultContentIs( "{\"amount\":15000.0,\"type\":\"shopping\",\"parent_id\":10}" );
    }

    @Test
    public void notPossibleToQueryingNonExistingTransaction() throws Exception {
        transactionControllerDriver.getTransaction( 10 )
                                   .resultStatusIs( HttpStatus.BAD_REQUEST )
                                   .resultContentIs( "Transaction '10' is not found!" );
    }

    @Test
    public void transactionRetrievedByType() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 11, rootTransaction( 2000, "shopping" ) )
                                   .putTransaction( 12, rootTransaction( 15000, "cars" ) )
                                   .getTransactionByType( "cars" )
                                   .resultStatusIs( HttpStatus.OK )
                                   .resultContentIs( "[10,12]" );
    }

    @Test
    public void retrieveByTypeReturnsEmptyListIfNoTransactionsBelongsToTheType() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 11, rootTransaction( 2000, "shopping" ) )
                                   .putTransaction( 12, rootTransaction( 15000, "cars" ) )
                                   .getTransactionByType( "not-existing-type" )
                                   .resultStatusIs( HttpStatus.OK )
                                   .resultContentIs( "[]" );
    }

    @Test
    public void sumOfTransactionChain() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 1000, "cars" ) )
                                   .putTransaction( 11, childTransaction( 2000, "shopping", 10L ) )
                                   .putTransaction( 12, rootTransaction( 3000, "cars" ) )
                                   .putTransaction( 13, childTransaction( 4000, "shopping", 12L ) )
                                   .putTransaction( 14, childTransaction( 5000, "cars", 10L ) )
                                   .putTransaction( 15, childTransaction( 6000, "shopping", 11L ) )
                                   .getSumOfTransactionChain( 10 )
                                   .resultStatusIs( HttpStatus.OK )
                                   .resultContentIs( "{\"sum\":14000.0}" );
    }

    @Test
    public void sumOfNotExistingTransactionChain() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 1000, "cars" ) )
                                   .getSumOfTransactionChain( 15 )
                                   .resultStatusIs( HttpStatus.BAD_REQUEST )
                                   .resultContentIs( "Transaction '15' is not found!" );
    }
}
