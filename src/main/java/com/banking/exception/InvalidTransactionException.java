package com.banking.exception;

/**
 * Thrown when a transaction request fails basic validation:
 * - null/blank transaction ID
 * - non-positive amount
 * - missing required account IDs for the operation type
 * - inactive account used as source/destination
 */
public class InvalidTransactionException extends BankingException {

    public InvalidTransactionException(String reason, String transactionId) {
        super(String.format("Invalid transaction [%s]: %s", transactionId, reason),
              "INVALID_TRANSACTION",
              transactionId);
    }

    // For account inactive case
    public static InvalidTransactionException accountInactive(String accountId, String transactionId) {
        return new InvalidTransactionException(
                String.format("Account [%s] is not ACTIVE", accountId),
                transactionId
        );
    }
}
