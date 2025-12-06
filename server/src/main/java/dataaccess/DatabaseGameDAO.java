package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {
    private static final String CREATE_STATEMENT =
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID INT PRIMARY KEY NOT NULL UNIQUE,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256) NOT NULL,
            chessGame LONGTEXT NOT NULL
            )
            """;

    public DatabaseGameDAO() throws DataAccessException {
        DatabaseManager.initializeTable(CREATE_STATEMENT);
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                var json = new Gson().toJson(gameData.game());
                preparedStatement.setString(5, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameName = resultSet.getString("gameName");
                        var json = resultSet.getString("chessGame");
                        var chessGame = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
        return null;
    }

    @Override
    public void updateGame(GameData updatedGameData) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, updatedGameData.whiteUsername());
                preparedStatement.setString(2, updatedGameData.blackUsername());
                preparedStatement.setString(3, updatedGameData.gameName());
                preparedStatement.setString(4, new Gson().toJson(updatedGameData.game()));
                preparedStatement.setInt(5, updatedGameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var gameID = resultSet.getInt("gameID");
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        var json = resultSet.getString("chessGame");
                        var chessGame = new Gson().fromJson(json, ChessGame.class);

                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
        return games;
    }

    @Override
    public void deleteGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database error");
        }
    }
}
