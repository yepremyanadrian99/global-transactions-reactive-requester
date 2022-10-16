package am.adrian.global.transactions.requester.transaction.service;

import am.adrian.global.transactions.requester.transaction.GlobalTransactionException;
import am.adrian.global.transactions.requester.transaction.domain.TransactionalOperation;
import am.adrian.global.transactions.requester.transaction.service.helper.ReactiveRequestContextHolder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class ReactiveTransactionalOperationExecutor {

    public <T, R> Mono<R> executeInsideTransaction(TransactionalOperation<T, Mono<R>> operation, T data) {
        return ReactiveRequestContextHolder.getTransactionalContext()
            .flatMap(context -> {
                try {
                    final var result = operation.apply(data);
                    context.addOperation(operation);
                    return result;
                } catch (Exception e) {
                    log.error("Exception happened during transactional execution", e);
                    log.info("Cascading the reverting of all operations in the context");

                    TransactionalOperation<?, ?> nextOperation;
                    while ((nextOperation = context.popOperation()) != null) {
                        nextOperation.revert();
                    }

                    return Mono.error(new GlobalTransactionException(e));
                }
            });
    }
}
