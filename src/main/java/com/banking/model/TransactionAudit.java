package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Audit record for each transaction processing attempt.
 */
public class TransactionAudit {

    private long auditId;
    private String transactionId;
    private String threadName;
    private String operation;
    private String result;
    private Long durationMs;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private LocalDateTime auditTimestamp;
    private String notes;

    public TransactionAudit() {}

    public TransactionAudit(String transactionId, String operation, String result,
                             Long durationMs, String threadName) {
        this.transactionId = transactionId;
        this.operation = operation;
        this.result = result;
        this.durationMs = durationMs;
        this.threadName = threadName;
        this.auditTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public long   getAuditId()                        { return auditId; }
    public void   setAuditId(long id)                 { this.auditId = id; }
    public String getTransactionId()                   { return transactionId; }
    public void   setTransactionId(String id)          { this.transactionId = id; }
    public String getThreadName()                      { return threadName; }
    public void   setThreadName(String t)              { this.threadName = t; }
    public String getOperation()                       { return operation; }
    public void   setOperation(String op)              { this.operation = op; }
    public String getResult()                          { return result; }
    public void   setResult(String r)                  { this.result = r; }
    public Long   getDurationMs()                      { return durationMs; }
    public void   setDurationMs(Long ms)               { this.durationMs = ms; }
    public BigDecimal getBalanceBefore()               { return balanceBefore; }
    public void   setBalanceBefore(BigDecimal b)       { this.balanceBefore = b; }
    public BigDecimal getBalanceAfter()                { return balanceAfter; }
    public void   setBalanceAfter(BigDecimal b)        { this.balanceAfter = b; }
    public LocalDateTime getAuditTimestamp()           { return auditTimestamp; }
    public void   setAuditTimestamp(LocalDateTime t)   { this.auditTimestamp = t; }
    public String getNotes()                           { return notes; }
    public void   setNotes(String notes)               { this.notes = notes; }
}
