package am.adrian.global.transactions.requester.config;

import am.adrian.global.transactions.service.ReactiveTransactionalOperationExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalTransactionsConfig {

    @Bean
    public ReactiveTransactionalOperationExecutor reactiveTransactionalOperationExecutor() {
        return new ReactiveTransactionalOperationExecutor();
    }
}
