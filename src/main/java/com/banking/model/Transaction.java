package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a banking transaction request and its final status.
 * transaction_id is unique and enforces idempotency at DB level.
 */
public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }

    public enum Status {
        PENDING, SUCCESS, FAILED, REJECTED, DUPLICATE
    }

    public enum Channel {
        ATM, INTERNET_BANKING, MOBILE_BANKING, BRANCH, SYSTEM
    }

    private String transactionId;
    private String sourceAccountId;
    private String destAccountId;
    private Type   transactionType;
    private BigDecimal amount;
    private Status status;
    private Channel channel;
    private String referenceNote;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String errorMessage;

    public Transaction() {}

    public Transaction(String transactionId, String sourceAccountId, String destAccountId,
                       Type type, BigDecimal amount, Channel channel) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destAccountId = destAccountId;
        this.transactionType = type;
        this.amount = amount;
        this.channel = channel;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Factory methods for each transaction type
    public static Transaction deposit(String txnId, String destAccountId, BigDecimal amount) {
        return new Transaction(txnId, null, destAccountId, Type.DEPOSIT, amount, Channel.SYSTEM);
    }

    public static Transaction withdrawal(String txnId, String sourceAccountId, BigDecimal amount) {
        return new Transaction(txnId, sourceAccountId, null, Type.WITHDRAWAL, amount, Channel.SYSTEM);
    }

    public static Transaction transfer(String txnId, String sourceAccountId, String destAccountId, BigDecimal amount) {
        return new Transaction(txnId, sourceAccountId, destAccountId, Type.TRANSFER, amount, Channel.SYSTEM);
    }

    // Getters and Setters
    public String getTransactionId()                       { return transactionId; }
    public void   setTransactionId(String id)              { this.transactionId = id; }
    public String getSourceAccountId()                     { return sourceAccountId; }
    public void   setSourceAccountId(String id)            { this.sourceAccountId = id; }
    public String getDestAccountId()                       { return destAccountId; }
    public void   setDestAccountId(String id)              { this.destAccountId = id; }
    public Type   getTransactionType()                     { return transactionType; }
    public void   setTransactionType(Type t)               { this.transactionType = t; }
    public BigDecimal getAmount()                          { return amount; }
    public void   setAmount(BigDecimal amount)             { this.amount = amount; }
    public Status getStatus()                              { return status; }
    public void   setStatus(Status status)                 { this.status = status; }
    public Channel getChannel()                            { return channel; }
    public void   setChannel(Channel channel)              { this.channel = channel; }
    public String getReferenceNote()                       { return referenceNote; }
    public void   setReferenceNote(String note)            { this.referenceNote = note; }
    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void   setCreatedAt(LocalDateTime t)            { this.createdAt = t; }
    public LocalDateTime getProcessedAt()                  { return processedAt; }
    public void   setProcessedAt(LocalDateTime t)          { this.processedAt = t; }
    public String getErrorMessage()                        { return errorMessage; }
    public void   setErrorMessage(String msg)              { this.errorMessage = msg; }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", type=" + transactionType
                + ", amount=" + amount + ", status=" + status + "}";
    }
}
