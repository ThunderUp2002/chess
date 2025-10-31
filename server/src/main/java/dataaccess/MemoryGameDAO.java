package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(String playerColor, int gameID, String username) throws DataAccessException {
        GameData gameData = games.get(gameID);
        games.remove(gameID);
        ChessGame game = new ChessGame();
        GameData newGameData;
        if (playerColor.equalsIgnoreCase("white")) {
            newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), game);
        }
        else {
            newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), game);
        }
        games.put(gameID, newGameData);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return this.games.values();
    }

    @Override
    public void deleteGames() throws DataAccessException {
        games.clear();
    }
}
