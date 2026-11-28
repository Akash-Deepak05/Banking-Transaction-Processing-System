package com.banking.exception;

/**
 * Thrown when a transaction with the same ID has already been processed.
 * Enforces idempotency - the caller should treat this as a benign duplicate.
 */
public class DuplicateTransactionException extends BankingException {

    public DuplicateTransactionException(String transactionId) {
        super(String.format("Duplicate transaction detected: [%s] - already processed", transactionId),
              "DUPLICATE_TRANSACTION",
              transactionId);
    }
}
