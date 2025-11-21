package com.zef.vehiclerenter.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

/**
 * Sebagai penghubung utama antara aplikasi dan database
 */
public final class DatabaseManager {
    private DatabaseManager() {}

    public static HikariDataSource createDataSource() {
        HikariConfig cfg = new HikariConfig();

        // Gunakan konfigurasi yang ada di `AppConfig.java`
        cfg.setJdbcUrl(AppConfig.DB_URL);
        cfg.setUsername(AppConfig.DB_USERNAME);
        cfg.setPassword(AppConfig.DB_PASSWORD);

        cfg.setPoolName("vehicle-renter-pool");
        cfg.setMaximumPoolSize(AppConfig.DB_POOL_SIZE);
        cfg.setConnectionTimeout(AppConfig.DB_CONNECTION_TIMEOUT);
        cfg.setIdleTimeout(AppConfig.DB_IDLE_TIMEOUT);
        cfg.setMaxLifetime(AppConfig.DB_MAX_LIFETIME);

        return new HikariDataSource(cfg);
    }

    public static DSLContext createDslContext(DataSource ds) {
        return DSL.using(ds, SQLDialect.POSTGRES);
    }
}
