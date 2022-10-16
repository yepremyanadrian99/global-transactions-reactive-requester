package am.adrian.global.transactions.requester.handler;

import static am.adrian.global.transactions.requester.constant.ErrorCodeConstants.INTERNAL_ERROR_CODE;
import static am.adrian.global.transactions.requester.constant.ErrorCodeConstants.RESTRICTED_WORDS_USED_ERROR_CODE;

import am.adrian.global.transactions.exception.GlobalTransactionException;
import am.adrian.global.transactions.requester.dto.response.ErrorResponse;
import am.adrian.global.transactions.requester.dto.response.RestrictedWordsErrorResponse;
import am.adrian.global.transactions.requester.dto.response.TextValidationResponse;
import am.adrian.global.transactions.requester.exception.RestrictedWordsUsedException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<RestrictedWordsErrorResponse> restrictedWordsUsedExceptionHandler(
        RestrictedWordsUsedException e
    ) {
        final var words = e.getViolations().stream()
            .map(TextValidationResponse.Violation::word)
            .toList();
        return ResponseEntity.badRequest()
            .body(
                new RestrictedWordsErrorResponse(
                    "error",
                    List.of(
                        new RestrictedWordsErrorResponse.Error(
                            words, RESTRICTED_WORDS_USED_ERROR_CODE, e.getMessage()
                        )
                    )
                )
            );
    }

    @ExceptionHandler
    public ResponseEntity<?> globalTransactionExceptionHandler(GlobalTransactionException e) {
        if (e.getCause() instanceof RestrictedWordsUsedException restrictedWordsUsedException) {
            return restrictedWordsUsedExceptionHandler(restrictedWordsUsedException);
        }

        return ResponseEntity.internalServerError()
            .body(
                new ErrorResponse(
                    "error",
                    List.of(
                        new ErrorResponse.Error(null, INTERNAL_ERROR_CODE, e.getCause().getMessage())
                    )
                )
            );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> defaultExceptionHandler(Exception e) {
        return ResponseEntity.internalServerError()
            .body(
                new ErrorResponse(
                    "error",
                    List.of(
                        new ErrorResponse.Error(null, INTERNAL_ERROR_CODE, e.getMessage())
                    )
                )
            );
    }
}
