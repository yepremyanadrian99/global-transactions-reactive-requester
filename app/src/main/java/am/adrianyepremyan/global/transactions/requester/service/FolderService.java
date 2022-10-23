package am.adrianyepremyan.global.transactions.requester.service;

import am.adrianyepremyan.global.transactions.domain.TransactionalOperation;
import am.adrianyepremyan.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrianyepremyan.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrianyepremyan.global.transactions.requester.mapper.FolderMapper;
import am.adrianyepremyan.global.transactions.requester.repository.FolderRepository;
import am.adrianyepremyan.global.transactions.service.ReactiveTransactionalOperationExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public record FolderService(
    UserService userService,
    TextValidationService textValidationService,
    ReactiveTransactionalOperationExecutor executor,
    FolderRepository repository
) {

    public Mono<FolderCreateResponse> create(FolderCreateRequest request) {
        return executor.executeInsideTransaction(createOperation(), request);
    }

    private TransactionalOperation<FolderCreateRequest, Mono<FolderCreateResponse>> createOperation() {
        return new TransactionalOperation<>() {
            @Override
            public Mono<FolderCreateResponse> apply(FolderCreateRequest request) {
                return Mono.just(request)
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(userService::findOrCreateUser)
                    .flatMap(textValidationService::validateTexts)
                    .map(FolderMapper.INSTANCE::toFolder)
                    .flatMap(repository::save)
                    .map(FolderMapper.INSTANCE::toCreateResponse);
            }

            @Override
            public void revert() {
                throw new UnsupportedOperationException("No revert operation is defined for folder create request");
            }
        };
    }
}
