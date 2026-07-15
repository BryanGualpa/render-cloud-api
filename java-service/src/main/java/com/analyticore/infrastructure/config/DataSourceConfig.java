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

        String url = databaseUrl;
        if (url.startsWith("postgres://")) {
            url = "postgresql://" + url.substring("postgres://".length());
        }

        if (System.getenv("RENDER") != null && url.contains(".oregon-postgres.render.com")) {
            url = url.replace(".oregon-postgres.render.com", "");
        }

        int atIndex = url.indexOf('@');
        if (atIndex < 0) {
            throw new IllegalStateException("DATABASE_URL inválida: falta @");
        }

        String userInfo = url.substring(url.indexOf("://") + 3, atIndex);
        String hostPart = url.substring(atIndex + 1);

        String username = userInfo;
        String password = "";
        int colonIndex = userInfo.indexOf(':');
        if (colonIndex >= 0) {
            username = userInfo.substring(0, colonIndex);
            password = userInfo.substring(colonIndex + 1);
        }

        int slashIndex = hostPart.indexOf('/');
        String hostAndPort = hostPart.substring(0, slashIndex);
        String database = hostPart.substring(slashIndex + 1);
        if (database.contains("?")) {
            database = database.substring(0, database.indexOf('?'));
        }

        String host;
        int port = 5432;
        int portColon = hostAndPort.lastIndexOf(':');
        if (portColon >= 0) {
            host = hostAndPort.substring(0, portColon);
            port = Integer.parseInt(hostAndPort.substring(portColon + 1));
        } else {
            host = hostAndPort;
        }

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(3);
        return new HikariDataSource(config);
    }
}
