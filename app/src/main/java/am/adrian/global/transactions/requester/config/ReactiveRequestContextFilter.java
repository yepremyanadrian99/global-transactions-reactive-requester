package am.adrian.global.transactions.requester.config;

import am.adrian.global.transactions.constant.ContextConstants;
import am.adrian.global.transactions.domain.TransactionalContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filter class responsible for creating an empty transactional context on each request.
 * This context will later hold all the required information regarding transactional operations, etc.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Log4j2
public class ReactiveRequestContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Initiating an empty transactional context");
        return chain.filter(exchange)
            .contextWrite(context -> context.put(ContextConstants.TRANSACTIONAL_CONTEXT, new TransactionalContext()));
    }
}
