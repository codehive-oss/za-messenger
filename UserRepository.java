import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository {

    private HikariDataSource dataSource;

    public UserRepository(String dbName) {
        dataSource = SqlitePool.getHikarDatasource(dbName);
    }

    public UserRepository() {
        this("dev.db");
    }

    public void createTable() {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("""
            CREATE TABLE IF NOT EXISTS user(
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                password TEXT NOT NULL
            )
            """)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
