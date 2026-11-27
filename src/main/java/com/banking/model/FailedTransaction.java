package com.banking.model;

import java.time.LocalDateTime;

/**
 * Failure details for a failed transaction, stored for investigation.
 */
public class FailedTransaction {

    public enum ErrorCategory {
        INSUFFICIENT_BALANCE,
        ACCOUNT_NOT_FOUND,
        INVALID_TRANSACTION,
        DUPLICATE_TRANSACTION,
        ACCOUNT_INACTIVE,
        SYSTEM_ERROR,
        DEADLOCK,
        TIMEOUT
    }

    private long id;
    private String transactionId;
    private ErrorCategory errorCategory;
    private String errorReason;
    private String stackTrace;
    private int retryCount;
    private LocalDateTime createdAt;

    public FailedTransaction() {}

    public FailedTransaction(String transactionId, ErrorCategory errorCategory, String errorReason) {
        this.transactionId = transactionId;
        this.errorCategory = errorCategory;
        this.errorReason = errorReason;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public long   getId()                              { return id; }
    public void   setId(long id)                       { this.id = id; }
    public String getTransactionId()                   { return transactionId; }
    public void   setTransactionId(String id)          { this.transactionId = id; }
    public ErrorCategory getErrorCategory()            { return errorCategory; }
    public void   setErrorCategory(ErrorCategory ec)   { this.errorCategory = ec; }
    public String getErrorReason()                     { return errorReason; }
    public void   setErrorReason(String r)             { this.errorReason = r; }
    public String getStackTrace()                      { return stackTrace; }
    public void   setStackTrace(String st)             { this.stackTrace = st; }
    public int    getRetryCount()                      { return retryCount; }
    public void   setRetryCount(int rc)                { this.retryCount = rc; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void   setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
}
