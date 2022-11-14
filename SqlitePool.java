import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlitePool {

    public static HikariDataSource getHikarDatasource(String dbName) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("QuboPool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:%s".formatted(dbName));
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000);
        config.setIdleTimeout(45000);
        config.setMaximumPoolSize(50);
        HikariDataSource dataSource = new HikariDataSource(config);
        try (Connection conn = dataSource.getConnection()) {
            conn.isValid(5 * 1000);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }

}
