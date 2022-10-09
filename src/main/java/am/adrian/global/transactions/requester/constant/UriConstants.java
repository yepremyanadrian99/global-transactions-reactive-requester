package am.adrian.global.transactions.requester.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UriConstants {

    public static final String TEXT_VALIDATOR_VALIDATE_TEXT_URI = "/internal/validate/text";
    public static final String USER_SERVICE_FIND_USER_URI = "/internal/user";
    public static final String USER_SERVICE_CREATE_USER_URI = "/internal/user";
}
