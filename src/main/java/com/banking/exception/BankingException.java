package com.banking.exception;

/**
 * Base exception for all banking domain exceptions.
 */
public class BankingException extends RuntimeException {

    private final String errorCategory;
    private final String transactionId;

    public BankingException(String message, String errorCategory, String transactionId) {
        super(message);
        this.errorCategory = errorCategory;
        this.transactionId = transactionId;
    }

    public BankingException(String message, String errorCategory, String transactionId, Throwable cause) {
        super(message, cause);
        this.errorCategory = errorCategory;
        this.transactionId = transactionId;
    }

    public String getErrorCategory() { return errorCategory; }
    public String getTransactionId() { return transactionId; }
}
