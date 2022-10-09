package am.adrian.global.transactions.requester.util;

import static am.adrian.global.transactions.requester.constant.UriConstants.TEXT_VALIDATOR_VALIDATE_TEXT_URI;

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
}
