package am.adrian.global.transactions.requester.dto.response;

public record FolderCreateResponse(
    String name,
    String description,
    Long userId
) {
}
