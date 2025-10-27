package dataaccess;

import model.*;
import chess.*;

public interface GameDAO {
    GameData createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ChessGame updateGame(String playerColor, int gameID, String username) throws DataAccessException;

    void deleteGames() throws DataAccessException;

    // GameList listGames() throws DataAccessException;
}
