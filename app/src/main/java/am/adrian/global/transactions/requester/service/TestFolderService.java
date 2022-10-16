package am.adrian.global.transactions.requester.service;

import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrian.global.transactions.domain.TransactionalOperation;
import am.adrian.global.transactions.service.ReactiveTransactionalOperationExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class TestFolderService {

    private static final TransactionalOperation<FolderCreateRequest, Mono<FolderCreateResponse>> createOperation =
        new TransactionalOperation<>() {
            private FolderCreateRequest cachedValue;

            @Override
            public Mono<FolderCreateResponse> apply(FolderCreateRequest request) {
                cachedValue = request;
                log.info("Creating folder inside a transaction");
                log.info("... but, an exception happened!");
                return Mono.error(new RuntimeException("Something went wrong!!!"));
//                return Mono.just(new FolderCreateResponse("Adrian", "Yepremyan", 123L));
            }

            @Override
            public void revert() {
                log.info("Reverting folder creation operation: " + cachedValue);
            }

            @Override
            public String toString() {
                return "Create folder operation: " + cachedValue;
            }
        };

    private final ReactiveTransactionalOperationExecutor executor;

    public Mono<FolderCreateResponse> create(FolderCreateRequest request, int count) {
        return Flux.range(0, count)
            .flatMap(i -> executor.executeInsideTransaction(new TransactionalOperation<Integer, Mono<Integer>>() {
                private Integer cachedValue;

                @Override
                public Mono<Integer> apply(Integer o) {
                    cachedValue = o;
                    log.info("Applying the transactional test operation: " + o);
                    return Mono.just(o);
                }

                @Override
                public void revert() {
                    log.info("Reverting the transactional operation: " + cachedValue);
                }

                @Override
                public String toString() {
                    return "Integer operation: " + cachedValue;
                }
            }, i))
            .collectList()
            .flatMap(integerMono -> executor.executeInsideTransaction(createOperation, request));
    }
}
