package service;

import dataaccess.*;

public class ClearService {

    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final UserDAO userAccess;

    public ClearService(AuthDAO authAccess, GameDAO gameAccess, UserDAO userAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clear() throws DataAccessException {
        authAccess.deleteAuths();
        gameAccess.deleteGames();
        userAccess.deleteUsers();
    }
}
