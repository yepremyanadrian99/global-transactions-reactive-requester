package am.adrianyepremyan.global.transactions.requester.service;

import static am.adrianyepremyan.global.transactions.requester.util.UriUtil.buildInternalFindUserUri;

import am.adrianyepremyan.global.transactions.domain.TransactionalOperation;
import am.adrianyepremyan.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrianyepremyan.global.transactions.requester.dto.request.UserCreateRequest;
import am.adrianyepremyan.global.transactions.requester.dto.response.UserResponse;
import am.adrianyepremyan.global.transactions.requester.util.UriUtil;
import am.adrianyepremyan.global.transactions.service.ReactiveTransactionalOperationExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public record UserService(
    ReactiveTransactionalOperationExecutor executor,
    WebClient userServiceClient
) {

    public Mono<FolderCreateRequest> findOrCreateUser(FolderCreateRequest request) {
        return executor.executeInsideTransaction(findOrCreateUserOperation(), request);
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

                log.info("Reverting the create user request");

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
