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
    public void updateGame(GameData updatedGameData) throws DataAccessException {
        int gameID = updatedGameData.gameID();
        games.remove(gameID);
        games.put(gameID, updatedGameData);
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
