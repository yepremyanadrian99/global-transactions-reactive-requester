package am.adrian.global.transactions.requester.dto.request;

public record UserCreateRequest(
    Long userId,
    String name,
    String surname
) {
}
