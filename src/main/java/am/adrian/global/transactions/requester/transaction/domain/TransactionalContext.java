package am.adrian.global.transactions.requester.transaction.domain;

import java.util.Stack;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@ToString
@Log4j2
public class TransactionalContext {

    private final Stack<TransactionalOperation<?, ?>> operations = new Stack<>();

    public void addOperation(TransactionalOperation<?, ?> operation) {
        log.info("Added new operation in the stack");
        operations.push(operation);
    }

    public TransactionalOperation<?, ?> popOperation() {
        if (operations.empty()) {
            log.info("No more operations in the stack!");
            return null;
        }
        return operations.pop();
    }


}
