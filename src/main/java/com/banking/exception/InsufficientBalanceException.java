package com.banking.exception;

import java.math.BigDecimal;

/**
 * Thrown when an account has insufficient balance for a debit operation.
 */
public class InsufficientBalanceException extends BankingException {

    private final BigDecimal currentBalance;
    private final BigDecimal requestedAmount;

    public InsufficientBalanceException(String accountId, BigDecimal currentBalance,
                                        BigDecimal requestedAmount, String transactionId) {
        super(String.format("Insufficient balance in account [%s]: available=%.2f, requested=%.2f",
                accountId, currentBalance, requestedAmount),
              "INSUFFICIENT_BALANCE",
              transactionId);
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getCurrentBalance()  { return currentBalance; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
}
