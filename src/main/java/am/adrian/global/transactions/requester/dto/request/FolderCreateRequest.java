package am.adrian.global.transactions.requester.dto.request;

public record FolderCreateRequest(
        String name,
        String description,
        Long userId
) {
}
