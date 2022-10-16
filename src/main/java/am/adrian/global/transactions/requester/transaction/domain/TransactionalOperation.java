package am.adrian.global.transactions.requester.transaction.domain;

public interface TransactionalOperation<T, R> {

    R apply(T data);

    void revert();
}
