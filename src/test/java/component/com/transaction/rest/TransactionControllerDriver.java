package component.com.transaction.rest;

import com.transaction.rest.TransactionController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.transaction.rest.TransactionController.TRANSACTION_ENDPOINT;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TransactionControllerDriver {

    private MockMvc mvc;

    private ResultActions resultAction;

    public TransactionControllerDriver() {
        mvc = MockMvcBuilders.standaloneSetup( new TransactionController() ).build();
    }

    public TransactionControllerDriver callGreeting() throws Exception {
        resultAction = mvc.perform( MockMvcRequestBuilders.get( TRANSACTION_ENDPOINT ).accept( MediaType.APPLICATION_JSON ) );
        return this;
    }

    public TransactionControllerDriver resultStatusIsOk() throws Exception {
        resultAction.andExpect( status().isOk() );
        return this;
    }

    public TransactionControllerDriver resultContentIs( String expectedContent ) throws Exception {
        resultAction.andExpect( content().string( equalTo( expectedContent ) ) );
        return this;
    }

}
