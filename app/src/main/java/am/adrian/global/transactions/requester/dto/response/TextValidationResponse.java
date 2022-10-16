package am.adrian.global.transactions.requester.dto.response;

import java.util.List;

public record TextValidationResponse(List<Violation> violations) {

    public record Violation(String word, String type) {
    }
}
