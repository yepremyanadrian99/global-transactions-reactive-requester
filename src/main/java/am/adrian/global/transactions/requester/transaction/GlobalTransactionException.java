package am.adrian.global.transactions.requester.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GlobalTransactionException extends RuntimeException {

    private final Exception cause;
}
