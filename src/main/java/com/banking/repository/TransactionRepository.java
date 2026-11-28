package com.banking.repository;

import com.banking.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * Data access object for Transaction (banking_transaction table).
 * The PRIMARY KEY on transaction_id enforces idempotency at the DB level.
 */
public class TransactionRepository {

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);
    private final DataSource dataSource;

    public TransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserts a new transaction record.
     * Throws SQLException with SQLState 23505 (unique violation) if duplicate.
     */
    public void insert(Connection conn, Transaction txn) throws SQLException {
        String sql = "INSERT INTO banking_transaction " +
                "(transaction_id, source_account_id, dest_account_id, transaction_type, " +
                " amount, status, channel, reference_note, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txn.getTransactionId());
            ps.setString(2, txn.getSourceAccountId());
            ps.setString(3, txn.getDestAccountId());
            ps.setString(4, txn.getTransactionType().name());
            ps.setBigDecimal(5, txn.getAmount());
            ps.setString(6, txn.getStatus().name());
            ps.setString(7, txn.getChannel() != null ? txn.getChannel().name() : "SYSTEM");
            ps.setString(8, txn.getReferenceNote());
            ps.executeUpdate();
        }
    }

    /**
     * Updates transaction status after processing.
     */
    public void updateStatus(Connection conn, String transactionId,
                              Transaction.Status status, String errorMessage) throws SQLException {
        String sql = "UPDATE banking_transaction SET status = ?, error_message = ?, processed_at = NOW() " +
                     "WHERE transaction_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, errorMessage);
            ps.setString(3, transactionId);
            ps.executeUpdate();
        }
    }

    /**
     * Checks whether a transaction ID already exists (idempotency check).
     */
    public boolean existsById(Connection conn, String transactionId) throws SQLException {
        String sql = "SELECT 1 FROM banking_transaction WHERE transaction_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retrieves a transaction by ID (read-only, uses own connection).
     */
    public Optional<Transaction> findById(String transactionId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            String sql = "SELECT * FROM banking_transaction WHERE transaction_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, transactionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapRow(rs));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getString("transaction_id"));
        t.setSourceAccountId(rs.getString("source_account_id"));
        t.setDestAccountId(rs.getString("dest_account_id"));
        t.setTransactionType(Transaction.Type.valueOf(rs.getString("transaction_type")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setStatus(Transaction.Status.valueOf(rs.getString("status")));
        String channel = rs.getString("channel");
        if (channel != null) t.setChannel(Transaction.Channel.valueOf(channel));
        t.setReferenceNote(rs.getString("reference_note"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) t.setCreatedAt(created.toLocalDateTime());
        Timestamp processed = rs.getTimestamp("processed_at");
        if (processed != null) t.setProcessedAt(processed.toLocalDateTime());
        t.setErrorMessage(rs.getString("error_message"));
        return t;
    }
}
