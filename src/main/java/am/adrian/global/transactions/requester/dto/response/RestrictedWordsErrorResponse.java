package am.adrian.global.transactions.requester.dto.response;

import java.util.List;

public record RestrictedWordsErrorResponse(String status, List<Error> errors) {

    public record Error(List<String> words, String code, String message) {
    }
}
