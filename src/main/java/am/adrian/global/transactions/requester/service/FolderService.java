package am.adrian.global.transactions.requester.service;

import am.adrian.global.transactions.requester.domain.Folder;
import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrian.global.transactions.requester.mapper.FolderMapper;
import am.adrian.global.transactions.requester.repository.FolderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public record FolderService(FolderRepository repository) {

    public Mono<FolderCreateResponse> create(FolderCreateRequest request) {
        return Mono.just(request)
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
