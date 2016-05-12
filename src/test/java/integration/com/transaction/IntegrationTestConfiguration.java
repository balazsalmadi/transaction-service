package integration.com.transaction;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan( basePackages = { "com.transaction", "integration.com.transaction" } )
public class IntegrationTestConfiguration {
}
