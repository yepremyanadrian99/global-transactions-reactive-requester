package am.adrian.global.transactions.requester.transaction.service;

import am.adrian.global.transactions.requester.transaction.GlobalTransactionException;
import am.adrian.global.transactions.requester.transaction.domain.TransactionalContext;
import am.adrian.global.transactions.requester.transaction.domain.TransactionalOperation;
import am.adrian.global.transactions.requester.transaction.service.helper.ReactiveRequestContextHolder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Class responsible for executing all instances of the {@link TransactionalOperation}.
 * This class should always be used for executing the operations,
 * otherwise they won't be cached and reverted later if exception happens at some step.
 */
@Service
@Log4j2
public class ReactiveTransactionalOperationExecutor {

    public <T, R> Mono<R> executeInsideTransaction(TransactionalOperation<T, Mono<R>> operation, T data) {
        return ReactiveRequestContextHolder.getTransactionalContext()
            .flatMap(context -> operation.apply(data)
                .doOnNext(result -> context.addOperation(operation))
                .onErrorResume(throwable -> handleError(context, throwable)));
    }

    private <R> Mono<R> handleError(TransactionalContext context, Throwable e) {
        log.error("Exception happened during transactional execution", e);
        log.info("Current context is: " + context.toString());
        log.info("Cascading the reverting of all operations in the context");

        TransactionalOperation<?, ?> nextOperation;
        while ((nextOperation = context.popOperation()) != null) {
            nextOperation.revert();
        }

        log.info("No more operations in the stack");

        return Mono.error(new GlobalTransactionException(e));
    }
}
