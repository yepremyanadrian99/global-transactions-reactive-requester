package am.adrian.global.transactions.requester.transaction.domain;

import java.util.Stack;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * The main class that holds context data, which includes the cache of operations.
 */
@ToString
@Log4j2
public class TransactionalContext {

    private final Stack<TransactionalOperation<?, ?>> operations = new Stack<>();

    public void addOperation(TransactionalOperation<?, ?> operation) {
        operations.push(operation);
    }

    public TransactionalOperation<?, ?> popOperation() {
        if (operations.empty()) {
            return null;
        }

        return operations.pop();
    }
}
