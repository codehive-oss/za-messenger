package db;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {

  private final HikariDataSource dataSource;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public UserRepository(String dbName) {
    dataSource = SqlitePool.getHikarDatasource(dbName);
  }

  public UserRepository() { this("dev.db"); }

  public void createTable() {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS user(
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                )
                """)) {
            stmt.executeUpdate();
  }
  catch (SQLException e) {
        throw new RuntimeException(e);
  }
}

public Optional<User> getUser(String name) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM user
                    WHERE username=?
                """)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
    User user = new User(resultSet.getString("username"),
                         resultSet.getString("password"));
    return Optional.of(user);
            }
}
catch (SQLException e) {
        logger.error(e.getMessage());
        return Optional.empty();
}
return Optional.empty();
}

public boolean createUser(String name, String password) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO user(username, password) VALUES (?,?)
                """)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            return stmt.executeUpdate()!=0;
}
catch (SQLException e) {
        logger.error(e.getMessage());
        return false;
}
}
}
