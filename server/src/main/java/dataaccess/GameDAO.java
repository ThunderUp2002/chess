package dataaccess;

import model.*;
import chess.*;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ChessGame updateGame(String playerColor, int gameID, String username) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteGames() throws DataAccessException;
}
