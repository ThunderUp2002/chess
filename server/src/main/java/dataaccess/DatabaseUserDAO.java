package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {
    private static final String CREATE_STATEMENT =
            """
            CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(256) PRIMARY KEY NOT NULL UNIQUE,
            hashedPassword VARCHAR(256) NOT NULL,
            email VARCHAR(256) NOT NULL
            )
            """;

    public DatabaseUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(CREATE_STATEMENT)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database configuration failed");
        }
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        var statement = "INSERT INTO users (username, hashedPassword, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, userData.email());
                int updatedRows = preparedStatement.executeUpdate();
                if (updatedRows == 0) {
                    throw new DataAccessException("Error: database error");
                }
            }
            return userData;
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, hashedPassword, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new UserData(resultSet.getString("username"), resultSet.getString("hashedPassword"), resultSet.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
        return null;
    }

    @Override
    public void deleteUsers() throws DataAccessException {
        var statement = "TRUNCATE users";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }
}
