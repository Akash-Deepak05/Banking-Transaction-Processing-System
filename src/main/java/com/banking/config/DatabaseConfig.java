package com.banking.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration using HikariCP connection pool.
 * Reads from application.properties on the classpath.
 */
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static volatile HikariDataSource dataSource;
    private static final Properties props = new Properties();

    static {
        try (InputStream is = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                log.warn("application.properties not found on classpath; using defaults");
            }
        } catch (IOException e) {
            log.error("Failed to load application.properties", e);
        }
    }

    private DatabaseConfig() {}

    /**
     * Returns the singleton DataSource. Thread-safe via double-checked locking.
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConfig.class) {
                if (dataSource == null) {
                    dataSource = createDataSource();
                }
            }
        }
        return dataSource;
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        // Environment variables take priority over application.properties
        // (allows Docker / cloud deployments to override without code changes)
        String dbUrl  = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");

        config.setJdbcUrl(dbUrl  != null ? dbUrl  : props.getProperty("db.url",      "jdbc:postgresql://localhost:5432/banking_db"));
        config.setUsername(dbUser != null ? dbUser : props.getProperty("db.username", "banking_user"));
        config.setPassword(dbPass != null ? dbPass : props.getProperty("db.password", "banking_pass"));
        config.setDriverClassName(props.getProperty("db.driver", "org.postgresql.Driver"));

        config.setPoolName(props.getProperty("db.pool.poolName", "BankingHikariPool"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxPoolSize", "20")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));

        // Important for PostgreSQL transactions
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

        // Connection test
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000);

        log.info("Initializing HikariCP pool: url={}, maxPool={}",
                config.getJdbcUrl(), config.getMaximumPoolSize());

        return new HikariDataSource(config);
    }

    public static Properties getProperties() {
        return props;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("HikariCP pool closed");
        }
    }
}
