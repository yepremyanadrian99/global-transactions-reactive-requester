package am.adrian.global.transactions.requester.exception;

import am.adrian.global.transactions.requester.dto.response.TextValidationResponse;
import java.util.List;
import lombok.Getter;

@Getter
public class RestrictedWordsUsedException extends FolderServiceException {

    private static final String MESSAGE = "Restricted words were used";

    private final List<TextValidationResponse.Violation> violations;

    public RestrictedWordsUsedException(List<TextValidationResponse.Violation> violations) {
        super(MESSAGE);
        this.violations = violations;
    }
}
