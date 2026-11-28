package com.banking.repository;

import com.banking.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

/**
 * Data access object for Account.
 * Uses pessimistic locking (SELECT FOR UPDATE) to prevent lost updates.
 */
public class AccountRepository {

    private static final Logger log = LoggerFactory.getLogger(AccountRepository.class);
    private final DataSource dataSource;

    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Finds an account by ID using the provided connection.
     * Uses SELECT FOR UPDATE to lock the row within the transaction (pessimistic locking).
     */
    public Optional<Account> findByIdForUpdate(Connection conn, String accountId) throws SQLException {
        String sql = "SELECT account_id, customer_id, account_type, balance, status, currency, version, " +
                     "created_at, updated_at FROM account WHERE account_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds an account by ID without locking (read-only queries).
     */
    public Optional<Account> findById(Connection conn, String accountId) throws SQLException {
        String sql = "SELECT account_id, customer_id, account_type, balance, status, currency, version, " +
                     "created_at, updated_at FROM account WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates account balance and increments version (optimistic lock check included via version).
     * Returns the number of rows updated (0 = optimistic lock conflict).
     */
    public int updateBalance(Connection conn, String accountId,
                              BigDecimal newBalance, long expectedVersion) throws SQLException {
        String sql = "UPDATE account SET balance = ?, version = version + 1, updated_at = NOW() " +
                     "WHERE account_id = ? AND version = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setString(2, accountId);
            ps.setLong(3, expectedVersion);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                log.warn("Optimistic lock conflict for account={} expectedVersion={}", accountId, expectedVersion);
            }
            return rows;
        }
    }

    /**
     * Atomic balance update using a single SQL statement with balance check.
     * This prevents negative balances without a separate SELECT.
     * Returns rows updated (0 if balance would go negative or account not found).
     */
    public int debitBalanceAtomic(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE account SET balance = balance - ?, version = version + 1, updated_at = NOW() " +
                     "WHERE account_id = ? AND status = 'ACTIVE' AND balance >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountId);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate();
        }
    }

    /**
     * Credits balance to an account.
     */
    public int creditBalance(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ?, version = version + 1, updated_at = NOW() " +
                     "WHERE account_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountId);
            return ps.executeUpdate();
        }
    }

    /**
     * Gets just the current balance (read committed).
     */
    public Optional<BigDecimal> getBalance(String accountId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            String sql = "SELECT balance FROM account WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, accountId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getBigDecimal("balance"));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getString("account_id"));
        a.setCustomerId(rs.getString("customer_id"));
        a.setAccountType(Account.AccountType.valueOf(rs.getString("account_type")));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setStatus(Account.Status.valueOf(rs.getString("status")));
        a.setCurrency(rs.getString("currency"));
        a.setVersion(rs.getLong("version"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) a.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) a.setUpdatedAt(updatedAt.toLocalDateTime());
        return a;
    }
}
