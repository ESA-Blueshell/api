package net.blueshell.api.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.annotation.ApplicationScope;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPDataSource {

    @Value("${spring.datasource.url}")
    private String url = "jdbc:mysql://localhost:3306/blueshell?autoReconnect=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    @Value("${spring.datasource.username}")
    private String username = "root";

    @Value("${spring.datasource.password}")
    private String password = "root";

    private HikariDataSource ds;

    public void init() {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setLeakDetectionThreshold(1000 * 15);
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("autoReconnect", "false");
        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.setAutoCommit(false);
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public HikariCPDataSource() {
        init();
    }
}
