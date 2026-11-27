package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bank account with current balance.
 * version field supports optimistic locking.
 */
public class Account {

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED, CLOSED
    }

    public enum AccountType {
        SAVINGS, CURRENT, FIXED_DEPOSIT
    }

    private String accountId;
    private String customerId;
    private AccountType accountType;
    private BigDecimal balance;
    private Status status;
    private String currency;
    private long version;      // optimistic lock version
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account() {}

    public Account(String accountId, String customerId, AccountType accountType, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.status = Status.ACTIVE;
        this.currency = "INR";
        this.version = 0L;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    // Getters and Setters
    public String getAccountId()                       { return accountId; }
    public void   setAccountId(String accountId)       { this.accountId = accountId; }
    public String getCustomerId()                      { return customerId; }
    public void   setCustomerId(String customerId)     { this.customerId = customerId; }
    public AccountType getAccountType()                { return accountType; }
    public void   setAccountType(AccountType t)        { this.accountType = t; }
    public BigDecimal getBalance()                     { return balance; }
    public void   setBalance(BigDecimal balance)       { this.balance = balance; }
    public Status getStatus()                          { return status; }
    public void   setStatus(Status status)             { this.status = status; }
    public String getCurrency()                        { return currency; }
    public void   setCurrency(String currency)         { this.currency = currency; }
    public long   getVersion()                         { return version; }
    public void   setVersion(long version)             { this.version = version; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void   setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void   setUpdatedAt(LocalDateTime t)        { this.updatedAt = t; }

    @Override
    public String toString() {
        return "Account{id=" + accountId + ", balance=" + balance + ", status=" + status + ", version=" + version + "}";
    }
}
