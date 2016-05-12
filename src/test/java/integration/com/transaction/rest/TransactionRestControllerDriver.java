package integration.com.transaction.rest;

import com.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static integration.com.transaction.TransactionStringFactory.transactionAsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TransactionRestControllerDriver {

    private MockMvc mvc;

    private ResultActions resultAction;

    @Autowired
    public TransactionRestControllerDriver( WebApplicationContext transactionService ) {
        mvc = MockMvcBuilders.webAppContextSetup( transactionService ).build();
    }

    public TransactionRestControllerDriver putTransaction( long transactionId, Transaction transaction ) throws Exception {
        resultAction = mvc.perform( MockMvcRequestBuilders.put( "/transactionservice/transaction/" + transactionId ).contentType( MediaType.APPLICATION_JSON ).content( transactionAsString( transaction ) ) );
        return this;
    }

    public TransactionRestControllerDriver getTransaction( long transactionId ) throws Exception {
        resultAction = mvc.perform( get( "/transactionservice/transaction/" + transactionId ) );
        return this;
    }

    public TransactionRestControllerDriver getTransactionByType( String type ) throws Exception {
        resultAction = mvc.perform( get( "/transactionservice/types/" + type ) );
        return this;
    }

    public TransactionRestControllerDriver getSumOfTransactionChain( long transactionId ) throws Exception {
        resultAction = mvc.perform( get( "/transactionservice/sum/" + transactionId ) );
        return this;
    }

    public TransactionRestControllerDriver resultStatusIs( HttpStatus status ) throws Exception {
        resultAction = resultAction.andExpect( status().is( status.value() ) );
        return this;
    }

    public TransactionRestControllerDriver resultContentIs( String expectedContent ) throws Exception {
        resultAction = resultAction.andExpect( MockMvcResultMatchers.content().string( is( expectedContent ) ) );
        return this;
    }
}
