package am.adrian.global.transactions.requester.service;

import am.adrian.global.transactions.requester.domain.Folder;
import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.request.TextValidationRequest;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrian.global.transactions.requester.dto.response.TextValidationResponse;
import am.adrian.global.transactions.requester.exception.RestrictedWordsUsedException;
import am.adrian.global.transactions.requester.mapper.FolderMapper;
import am.adrian.global.transactions.requester.repository.FolderRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public record FolderService(FolderRepository repository, WebClient textValidationClient) {

    public Mono<FolderCreateResponse> create(FolderCreateRequest request) {
        return Mono.just(request)
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(folderRequest -> textValidationClient.post()
                .body(BodyInserters.fromValue(new TextValidationRequest(request.name())))
                .retrieve()
                .bodyToMono(TextValidationResponse.class)
                .filter(Objects::nonNull)
                .doOnNext(response -> {
                    if (!CollectionUtils.isEmpty(response.violations())) {
                        throw new RestrictedWordsUsedException(response.violations());
                    }
                })
                .block())
            .map(folderRequest -> {
                final var folder = new Folder();
                folder.setName(folderRequest.name());
                folder.setDescription(folderRequest.description());
                folder.setUserId(folderRequest.userId());
                return folder;
            })
            .flatMap(repository::save)
            .map(FolderMapper.INSTANCE::toCreateResponse);
    }
}
