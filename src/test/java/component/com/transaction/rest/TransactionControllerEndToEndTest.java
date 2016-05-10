package component.com.transaction.rest;

import component.com.transaction.ComponentTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration( ComponentTestConfiguration.class )
@WebAppConfiguration
public class TransactionControllerEndToEndTest {

    @Autowired
    private TransactionControllerDriver transactionControllerDriver;

    @Test
    public void greetingEndToEnd() throws Exception {
        transactionControllerDriver.callGreeting()
                                   .resultStatusIsOk()
                                   .resultContentIs( "Hello world!" );
    }
}
