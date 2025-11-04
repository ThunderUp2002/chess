package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String playerColor, int gameID, String username) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteGames() throws DataAccessException {

    }
}
