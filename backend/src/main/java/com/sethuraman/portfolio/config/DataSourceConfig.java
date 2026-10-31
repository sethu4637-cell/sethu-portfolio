package com.sethuraman.portfolio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Intelligent Data Source Configuration.
 *
 * 1. Checks MySQL connection using the configured spring.datasource.* properties.
 * 2. If MySQL is connected and credentials are valid, connects directly to MySQL.
 * 3. If MySQL credentials are missing or access is denied, automatically falls back
 *    to an embedded local persistent database (H2 in MySQL-mode) so the fullstack application
 *    runs immediately without failing or requiring instant manual DB setup.
 */
@Configuration
@Profile("!test")
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Testing MySQL connectivity at: {}", mysqlUrl);

        boolean mysqlAvailable = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
                mysqlAvailable = true;
                log.info("MySQL connection established successfully! Using MySQL database.");
            }
        } catch (Exception e) {
            log.warn("MySQL connection failed ({}: {}).", e.getClass().getSimpleName(), e.getMessage());
        }

        if (mysqlAvailable) {
            return DataSourceBuilder.create()
                    .url(mysqlUrl)
                    .username(mysqlUsername)
                    .password(mysqlPassword)
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        }

        // Automatic local fallback
        log.info("==========================================================================");
        log.info("▶ FALLBACK: Starting embedded persistent database (portfolio_local_db)");
        log.info("  To switch to MySQL, update spring.datasource.password in application.properties");
        log.info("==========================================================================");

        String localDbUrl = "jdbc:h2:file:./portfolio_local_db;DB_CLOSE_DELAY=-1;MODE=MySQL;AUTO_SERVER=TRUE";
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = DriverManager.getConnection(localDbUrl, "sa", "")) {
                log.info("H2 persistent database initialized successfully with AUTO_SERVER mode.");
            }
        } catch (Exception e) {
            log.warn("Persistent H2 file database connection failed ({}: {}). Switching to in-memory fallback.",
                    e.getClass().getSimpleName(), e.getMessage());
            localDbUrl = "jdbc:h2:mem:portfolio_local_db;DB_CLOSE_DELAY=-1;MODE=MySQL";
        }

        return DataSourceBuilder.create()
                .url(localDbUrl)
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }
}
