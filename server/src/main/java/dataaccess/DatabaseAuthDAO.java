package dataaccess;

import exceptions.UnauthorizedException;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class DatabaseAuthDAO implements AuthDAO {
    private static final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(256) PRIMARY KEY NOT NULL UNIQUE,
            username VARCHAR(256) NOT NULL
            )
            """;

    public DatabaseAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database configuration failed");
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
            return authData;
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new AuthData(resultSet.getString("authToken"), resultSet.getString("username"));
                    }
                    else {
                        throw new UnauthorizedException("Error: unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                int deletedRows = preparedStatement.executeUpdate();
                if (deletedRows == 0) {
                    throw new UnauthorizedException("Error: unauthorized");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public void deleteAuths() throws DataAccessException {
        var statement = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }
}
