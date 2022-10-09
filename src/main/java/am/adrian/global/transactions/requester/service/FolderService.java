package am.adrian.global.transactions.requester.service;

import static am.adrian.global.transactions.requester.util.UriUtil.buildInternalFindUserUri;

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
    WebClient userServiceClient
) {

    public Mono<FolderCreateResponse> create(FolderCreateRequest request) {
        return Mono.just(request)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::findOrCreateUser)
            // Here if validateTexts throws error - user can be created redundantly!
            .flatMap(this::validateTexts)
            .map(FolderMapper.INSTANCE::toFolder)
            .flatMap(repository::save)
            .map(FolderMapper.INSTANCE::toCreateResponse);
    }

    private Mono<FolderCreateRequest> findOrCreateUser(FolderCreateRequest request) {
        return userServiceClient.get()
            .uri((builder) -> buildInternalFindUserUri(builder, request.userId()))
            .retrieve()
            .bodyToMono(UserResponse.class)
            .onErrorResume(throwable -> {
                log.info("Error on finding user");
                final var createRequest = new UserCreateRequest(
                    request.userId(),
                    request.name(),
                    request.description()
                );
                return userServiceClient.post()
                    .uri(UriUtil::buildInternalCreateUserUri)
                    .body(BodyInserters.fromValue(createRequest))
                    .retrieve()
                    .bodyToMono(UserResponse.class);
            })
            .thenReturn(request);
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
}
