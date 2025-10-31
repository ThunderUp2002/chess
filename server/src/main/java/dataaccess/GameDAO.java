package dataaccess;

import model.*;
import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(String playerColor, int gameID, String username) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteGames() throws DataAccessException;
}
