package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new DatabaseGameDAO();
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        gameDAO.deleteGames();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int gameID = 1;
        String whiteUsername = "whiteUsername";
        String blackUsername = "blackUsername";
        String gameName = "gameName";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

        gameDAO.createGame(gameData);
        GameData gameDataTest = gameDAO.getGame(gameID);

        assertNotNull(gameDataTest);
    }

    @Test
    public void createGameFailure() {
        assertThrows(NullPointerException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        int gameID = 1;
        String whiteUsername = "whiteUsername";
        String blackUsername = "blackUsername";
        String gameName = "gameName";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        gameDAO.createGame(gameData);
        assertEquals(gameData.gameID(), gameDAO.getGame(1).gameID());
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        assertNull(gameDAO.getGame(-1));
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        int gameID = 1;
        String gameName = "game1";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
        gameDAO.createGame(gameData);
        GameData updatedGameData = new GameData(gameID, "username", null, gameName, chessGame);
        gameDAO.updateGame(updatedGameData);
        GameData newGameData = gameDAO.getGame(1);
        assertEquals("username", newGameData.whiteUsername());
    }

    @Test
    public void updateGameFailure() {
        assertThrows(NullPointerException.class, () -> gameDAO.updateGame(null));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, null, null, "game3", new ChessGame()));
        assertNotNull(gameDAO.listGames());
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, null, null, "game3", new ChessGame()));

        gameDAO.deleteGames();

        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void deleteGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, null, null, "game3", new ChessGame()));

        gameDAO.deleteGames();

        assertNull(gameDAO.getGame(1));
        assertNull(gameDAO.getGame(2));
        assertNull(gameDAO.getGame(3));
    }
}
