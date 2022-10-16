package am.adrian.global.transactions.requester.transaction.service.helper;

import static am.adrian.global.transactions.requester.transaction.constant.ContextConstants.TRANSACTIONAL_CONTEXT;

import am.adrian.global.transactions.requester.transaction.domain.TransactionalContext;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextHolder {

    public static Mono<TransactionalContext> getTransactionalContext() {
        return Mono.deferContextual(Mono::just)
            .map(contextView -> contextView.get(TRANSACTIONAL_CONTEXT));
    }
}
