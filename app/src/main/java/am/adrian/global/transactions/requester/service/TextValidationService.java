package am.adrian.global.transactions.requester.service;

import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.request.TextValidationRequest;
import am.adrian.global.transactions.requester.dto.response.TextValidationResponse;
import am.adrian.global.transactions.requester.exception.RestrictedWordsUsedException;
import am.adrian.global.transactions.requester.util.UriUtil;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public record TextValidationService(WebClient textValidationClient) {

    public Mono<FolderCreateRequest> validateTexts(FolderCreateRequest request) {
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
