package com.banking.repository;

import com.banking.model.FailedTransaction;
import com.banking.model.TransactionAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

/**
 * Data access object for audit and failure records.
 */
public class AuditRepository {

    private static final Logger log = LoggerFactory.getLogger(AuditRepository.class);
    private final DataSource dataSource;

    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserts an audit record using the provided connection (within the same DB transaction).
     */
    public void insertAudit(Connection conn, TransactionAudit audit) throws SQLException {
        String sql = "INSERT INTO transaction_audit " +
                "(transaction_id, thread_name, operation, result, duration_ms, " +
                " balance_before, balance_after, audit_timestamp, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, audit.getTransactionId());
            ps.setString(2, audit.getThreadName());
            ps.setString(3, audit.getOperation());
            ps.setString(4, audit.getResult());
            if (audit.getDurationMs() != null) {
                ps.setLong(5, audit.getDurationMs());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            if (audit.getBalanceBefore() != null) {
                ps.setBigDecimal(6, audit.getBalanceBefore());
            } else {
                ps.setNull(6, Types.NUMERIC);
            }
            if (audit.getBalanceAfter() != null) {
                ps.setBigDecimal(7, audit.getBalanceAfter());
            } else {
                ps.setNull(7, Types.NUMERIC);
            }
            ps.setString(8, audit.getNotes());
            ps.executeUpdate();
        }
    }

    /**
     * Inserts a failed transaction record (uses its own connection after a rollback).
     */
    public void insertFailure(FailedTransaction failure) {
        String sql = "INSERT INTO failed_transaction " +
                "(transaction_id, error_category, error_reason, stack_trace, retry_count, created_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW()) " +
                "ON CONFLICT (transaction_id) DO UPDATE SET " +
                "  retry_count = failed_transaction.retry_count + 1, " +
                "  error_reason = EXCLUDED.error_reason";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            ps.setString(1, failure.getTransactionId());
            ps.setString(2, failure.getErrorCategory().name());
            ps.setString(3, failure.getErrorReason());
            ps.setString(4, failure.getStackTrace());
            ps.setInt(5, failure.getRetryCount());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Log but don't rethrow - failure recording must not mask the original error
            log.error("Failed to persist failure record for txn={}: {}",
                    failure.getTransactionId(), e.getMessage());
        }
    }
}
