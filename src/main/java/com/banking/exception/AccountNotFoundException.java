package com.banking.exception;

/**
 * Thrown when the requested account does not exist in the system.
 */
public class AccountNotFoundException extends BankingException {

    private final String accountId;

    public AccountNotFoundException(String accountId, String transactionId) {
        super(String.format("Account not found: [%s]", accountId),
              "ACCOUNT_NOT_FOUND",
              transactionId);
        this.accountId = accountId;
    }

    public String getAccountId() { return accountId; }
}
