package component.com.transaction.rest;

import component.com.transaction.ComponentTestConfiguration;
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
@ContextConfiguration( classes = { ComponentTestConfiguration.class } )
@WebAppConfiguration
@DirtiesContext( classMode = BEFORE_EACH_TEST_METHOD )
public class TransactionServiceEndToEndTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private TransactionControllerDriver transactionControllerDriver;

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
                                   .resultStatusIs( HttpStatus.CONFLICT )
                                   .resultContentIs( "Parent transaction '10' is missing!" );
    }

    @Test
    public void transactionChainCanBeCreated() throws Exception {
        transactionControllerDriver.putTransaction( 10, rootTransaction( 5000, "cars" ) )
                                   .putTransaction( 11, childTransaction( 10000, "shopping", 10L ) )
                                   .resultStatusIs( HttpStatus.CREATED );
    }
}