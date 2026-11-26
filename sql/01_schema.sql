-- ============================================================
-- Banking Transaction System - DDL Schema
-- PostgreSQL compatible
-- ============================================================

-- ============================================================
-- DROP TABLES (for re-creation)
-- ============================================================
DROP TABLE IF EXISTS failed_transaction CASCADE;
DROP TABLE IF EXISTS transaction_audit CASCADE;
DROP TABLE IF EXISTS banking_transaction CASCADE;
DROP TABLE IF EXISTS account CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

-- ============================================================
-- CUSTOMER TABLE
-- ============================================================
CREATE TABLE customer (
    customer_id     VARCHAR(20)     PRIMARY KEY,
    full_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    UNIQUE NOT NULL,
    phone           VARCHAR(20),
    address         VARCHAR(255),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_customer_email CHECK (email LIKE '%@%'),
    CONSTRAINT chk_customer_phone CHECK (phone IS NULL OR LENGTH(phone) >= 7)
);

COMMENT ON TABLE customer IS 'Master table for bank customers';

-- ============================================================
-- ACCOUNT TABLE
-- ============================================================
CREATE TABLE account (
    account_id      VARCHAR(20)     PRIMARY KEY,
    customer_id     VARCHAR(20)     NOT NULL,
    account_type    VARCHAR(20)     NOT NULL DEFAULT 'SAVINGS',
    balance         NUMERIC(18, 2)  NOT NULL DEFAULT 0.00,
    status          VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    currency        VARCHAR(3)      NOT NULL DEFAULT 'INR',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT          NOT NULL DEFAULT 0,   -- for optimistic locking

    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
    CONSTRAINT chk_account_balance  CHECK (balance >= 0),
    CONSTRAINT chk_account_type     CHECK (account_type IN ('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT')),
    CONSTRAINT chk_account_status   CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED')),
    CONSTRAINT chk_account_currency CHECK (currency IN ('INR', 'USD', 'EUR', 'GBP'))
);

COMMENT ON TABLE account IS 'Bank accounts with current balance';
COMMENT ON COLUMN account.version IS 'Optimistic lock version counter';

-- ============================================================
-- BANKING_TRANSACTION TABLE
-- (named banking_transaction to avoid SQL keyword conflict)
-- ============================================================
CREATE TABLE banking_transaction (
    transaction_id      VARCHAR(50)     PRIMARY KEY,
    source_account_id   VARCHAR(20),
    dest_account_id     VARCHAR(20),
    transaction_type    VARCHAR(20)     NOT NULL,
    amount              NUMERIC(18, 2)  NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    channel             VARCHAR(20)     NOT NULL DEFAULT 'SYSTEM',
    reference_note      VARCHAR(255),
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at        TIMESTAMP,
    error_message       VARCHAR(500),

    CONSTRAINT fk_txn_source_account FOREIGN KEY (source_account_id) REFERENCES account(account_id),
    CONSTRAINT fk_txn_dest_account   FOREIGN KEY (dest_account_id)   REFERENCES account(account_id),
    CONSTRAINT chk_txn_amount        CHECK (amount > 0),
    CONSTRAINT chk_txn_type          CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    CONSTRAINT chk_txn_status        CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REJECTED', 'DUPLICATE')),
    CONSTRAINT chk_txn_channel       CHECK (channel IN ('ATM', 'INTERNET_BANKING', 'MOBILE_BANKING', 'BRANCH', 'SYSTEM')),
    CONSTRAINT chk_txn_accounts      CHECK (
        (transaction_type = 'DEPOSIT'    AND dest_account_id IS NOT NULL AND source_account_id IS NULL) OR
        (transaction_type = 'WITHDRAWAL' AND source_account_id IS NOT NULL AND dest_account_id IS NULL) OR
        (transaction_type = 'TRANSFER'   AND source_account_id IS NOT NULL AND dest_account_id IS NOT NULL
                                         AND source_account_id <> dest_account_id)
    )
);

COMMENT ON TABLE banking_transaction IS 'All transaction requests and their final status';

-- ============================================================
-- TRANSACTION_AUDIT TABLE
-- ============================================================
CREATE TABLE transaction_audit (
    audit_id            BIGSERIAL       PRIMARY KEY,
    transaction_id      VARCHAR(50)     NOT NULL,
    thread_name         VARCHAR(100)    NOT NULL,
    operation           VARCHAR(50)     NOT NULL,
    result              VARCHAR(20)     NOT NULL,
    duration_ms         BIGINT,
    balance_before      NUMERIC(18, 2),
    balance_after       NUMERIC(18, 2),
    audit_timestamp     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes               VARCHAR(500),

    CONSTRAINT fk_audit_transaction FOREIGN KEY (transaction_id) REFERENCES banking_transaction(transaction_id),
    CONSTRAINT chk_audit_result CHECK (result IN ('SUCCESS', 'FAILED', 'REJECTED', 'DUPLICATE'))
);

COMMENT ON TABLE transaction_audit IS 'Processing audit trail for every transaction attempt';

-- ============================================================
-- FAILED_TRANSACTION TABLE
-- ============================================================
CREATE TABLE failed_transaction (
    id                  BIGSERIAL       PRIMARY KEY,
    transaction_id      VARCHAR(50)     NOT NULL UNIQUE,
    error_category      VARCHAR(50)     NOT NULL,
    error_reason        VARCHAR(500)    NOT NULL,
    stack_trace         TEXT,
    retry_count         INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_failed_transaction FOREIGN KEY (transaction_id) REFERENCES banking_transaction(transaction_id),
    CONSTRAINT chk_error_category CHECK (error_category IN (
        'INSUFFICIENT_BALANCE', 'ACCOUNT_NOT_FOUND', 'INVALID_TRANSACTION',
        'DUPLICATE_TRANSACTION', 'ACCOUNT_INACTIVE', 'SYSTEM_ERROR',
        'DEADLOCK', 'TIMEOUT'
    ))
);

COMMENT ON TABLE failed_transaction IS 'Failure details for investigation and retry';

-- ============================================================
-- INDEXES
-- ============================================================

-- customer indexes
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_active ON customer(is_active) WHERE is_active = TRUE;

-- account indexes
CREATE INDEX idx_account_customer_id ON account(customer_id);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_account_customer_status ON account(customer_id, status);

-- transaction indexes (most critical for performance)
CREATE INDEX idx_txn_source_account ON banking_transaction(source_account_id);
CREATE INDEX idx_txn_dest_account   ON banking_transaction(dest_account_id);
CREATE INDEX idx_txn_status         ON banking_transaction(status);
CREATE INDEX idx_txn_type           ON banking_transaction(transaction_type);
CREATE INDEX idx_txn_created_at     ON banking_transaction(created_at);
-- Composite index for account transaction history queries
CREATE INDEX idx_txn_source_created  ON banking_transaction(source_account_id, created_at DESC);
CREATE INDEX idx_txn_dest_created    ON banking_transaction(dest_account_id, created_at DESC);
-- Partial index for pending transactions
CREATE INDEX idx_txn_pending ON banking_transaction(created_at) WHERE status = 'PENDING';

-- audit indexes
CREATE INDEX idx_audit_transaction_id  ON transaction_audit(transaction_id);
CREATE INDEX idx_audit_timestamp       ON transaction_audit(audit_timestamp);
CREATE INDEX idx_audit_result          ON transaction_audit(result);

-- failed transaction indexes
CREATE INDEX idx_failed_error_category ON failed_transaction(error_category);
CREATE INDEX idx_failed_created_at     ON failed_transaction(created_at);
