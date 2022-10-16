package am.adrian.global.transactions.requester.util;

import static am.adrian.global.transactions.requester.constant.UriConstants.TEXT_VALIDATOR_VALIDATE_TEXT_URI;
import static am.adrian.global.transactions.requester.constant.UriConstants.USER_SERVICE_BASE_URI;

import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UriUtil {

    public static String normalizeUrl(String url) {
        if (url != null && !(url.startsWith("http://") || url.startsWith("https://"))) {
            return "http://" + url;
        }

        return url;
    }

    public static URI buildInternalValidateTextUri(UriBuilder uriBuilder) {
        return uriBuilder.path(TEXT_VALIDATOR_VALIDATE_TEXT_URI).build();
    }

    public static URI buildInternalFindUserUri(UriBuilder uriBuilder, Long userId) {
        return uriBuilder.path(USER_SERVICE_BASE_URI).path("/" + userId).build();
    }

    public static URI buildInternalCreateUserUri(UriBuilder uriBuilder) {
        return uriBuilder.path(USER_SERVICE_BASE_URI).build();
    }

    public static URI buildInternalDeleteUserUri(UriBuilder uriBuilder, Long userId) {
        return uriBuilder.path(USER_SERVICE_BASE_URI).path("/" + userId).build();
    }
}
