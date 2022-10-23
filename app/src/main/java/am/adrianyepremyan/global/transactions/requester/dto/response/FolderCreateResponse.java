package am.adrianyepremyan.global.transactions.requester.dto.response;

public record FolderCreateResponse(
    String name,
    String description,
    Long userId
) {
}
