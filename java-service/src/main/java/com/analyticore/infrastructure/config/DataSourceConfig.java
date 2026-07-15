package com.analyticore.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException("DATABASE_URL no está configurada");
        }

        String jdbcUrl = databaseUrl;
        if (jdbcUrl.startsWith("postgres://")) {
            jdbcUrl = "jdbc:postgresql://" + jdbcUrl.substring("postgres://".length());
        } else if (jdbcUrl.startsWith("postgresql://")) {
            jdbcUrl = "jdbc:postgresql://" + jdbcUrl.substring("postgresql://".length());
        }

        if (System.getenv("RENDER") != null && jdbcUrl.contains(".oregon-postgres.render.com")) {
            jdbcUrl = jdbcUrl.replace(".oregon-postgres.render.com", "");
        } else if (!jdbcUrl.contains("sslmode=")) {
            jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setMaximumPoolSize(3);
        return new HikariDataSource(config);
    }
}
