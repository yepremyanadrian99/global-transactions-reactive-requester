package am.adrian.global.transactions.requester.service;

import static am.adrian.global.transactions.requester.util.UriUtil.buildInternalFindUserUri;

import am.adrian.global.transactions.domain.TransactionalOperation;
import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.request.TextValidationRequest;
import am.adrian.global.transactions.requester.dto.request.UserCreateRequest;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrian.global.transactions.requester.dto.response.TextValidationResponse;
import am.adrian.global.transactions.requester.dto.response.UserResponse;
import am.adrian.global.transactions.requester.exception.RestrictedWordsUsedException;
import am.adrian.global.transactions.requester.mapper.FolderMapper;
import am.adrian.global.transactions.requester.repository.FolderRepository;
import am.adrian.global.transactions.requester.util.UriUtil;
import am.adrian.global.transactions.service.ReactiveTransactionalOperationExecutor;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public record FolderService(
    FolderRepository repository,
    WebClient textValidationClient,
    WebClient userServiceClient,
    ReactiveTransactionalOperationExecutor transactionalExecutor
) {

    public Mono<FolderCreateResponse> create(FolderCreateRequest request) {
        return transactionalExecutor.executeInsideTransaction(createOperation(), request);
    }

    private Mono<FolderCreateRequest> findOrCreateUser(FolderCreateRequest request) {
        return transactionalExecutor.executeInsideTransaction(findOrCreateUserOperation(), request);
    }

    private Mono<FolderCreateRequest> validateTexts(FolderCreateRequest request) {
        final var body = new TextValidationRequest(request.name());
        return textValidationClient.post()
            .uri(UriUtil::buildInternalValidateTextUri)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .bodyToMono(TextValidationResponse.class)
            .filter(Objects::nonNull)
            .doOnNext(response -> {
                if (!CollectionUtils.isEmpty(response.violations())) {
                    throw new RestrictedWordsUsedException(response.violations());
                }
            })
            .thenReturn(request);
    }

    private TransactionalOperation<FolderCreateRequest, Mono<FolderCreateResponse>> createOperation() {
        return new TransactionalOperation<>() {
            @Override
            public Mono<FolderCreateResponse> apply(FolderCreateRequest request) {
                return Mono.just(request)
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(FolderService.this::findOrCreateUser)
                    .flatMap(FolderService.this::validateTexts)
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

    private TransactionalOperation<FolderCreateRequest, Mono<FolderCreateRequest>> findOrCreateUserOperation() {
        return new TransactionalOperation<>() {
            private UserCreateRequest cachedUserRequest;

            @Override
            public Mono<FolderCreateRequest> apply(FolderCreateRequest request) {
                return userServiceClient.get()
                    .uri((builder) -> buildInternalFindUserUri(builder, request.userId()))
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .onErrorResume(throwable -> {
                        log.info("User is not found, creating one now...");
                        final var createRequest = new UserCreateRequest(
                            request.userId(),
                            request.name(),
                            request.description()
                        );
                        final var result = userServiceClient.post()
                            .uri(UriUtil::buildInternalCreateUserUri)
                            .body(BodyInserters.fromValue(createRequest))
                            .retrieve()
                            .bodyToMono(UserResponse.class);
                        // Cache the request if it was successful
                        cachedUserRequest = createRequest;
                        log.info("User create request is cached");
                        return result;
                    })
                    .thenReturn(request);
            }

            @Override
            public void revert() {
                if (cachedUserRequest == null) {
                    log.info("User was not cached, nothing to revert");
                    return;
                }

                final var userId = cachedUserRequest.userId();

                log.info("Reverting the creat user request");

                userServiceClient.delete()
                    .uri(builder -> UriUtil.buildInternalDeleteUserUri(builder, userId))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
                log.info("Finished reverting the create user request");
            }

            @Override
            public String toString() {
                return "Create user operation: " + cachedUserRequest;
            }
        };
    }
}
