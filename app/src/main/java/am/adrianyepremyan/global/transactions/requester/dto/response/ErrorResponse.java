package am.adrianyepremyan.global.transactions.requester.dto.response;

import java.util.List;

public record ErrorResponse(String status, List<Error> errors) {

    public record Error(String field, String code, String message) {
    }
}
