package am.adrianyepremyan.global.transactions.requester.config;

import am.adrianyepremyan.global.transactions.service.ReactiveTransactionalOperationExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalTransactionsConfig {

    @Bean
    public ReactiveTransactionalOperationExecutor reactiveTransactionalOperationExecutor() {
        return new ReactiveTransactionalOperationExecutor();
    }
}
