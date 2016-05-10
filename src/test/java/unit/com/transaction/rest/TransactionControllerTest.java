package unit.com.transaction.rest;

import com.transaction.rest.TransactionController;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TransactionControllerTest {

    @Test
    public void greetingReturnsHelloWorld() throws Exception {

        TransactionController transactionController = new TransactionController();
        String greeting = transactionController.greeting();

        assertThat( greeting, is( "Hello world!" ) );
    }
}
