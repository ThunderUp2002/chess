package dataaccess;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ChessGame updateGame(String playerColor, int gameID, String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteGames() throws DataAccessException {

    }
}
