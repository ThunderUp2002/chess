package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private ClearService clearService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        gameDAO = new DatabaseGameDAO();
        userDAO = new DatabaseUserDAO();
        clearService = new ClearService(authDAO, gameDAO, userDAO);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("username1", "password1", "email1@gmail.com"));
        userDAO.createUser(new UserData("username2", "password2", "email2@gmail.com"));
        userDAO.createUser(new UserData("username3", "password3", "email3@gmail.com"));

        AuthData authData1 = authDAO.createAuth("username1");
        AuthData authData2 = authDAO.createAuth("username2");
        AuthData authData3 = authDAO.createAuth("username3");

        String authToken1 = authData1.authToken();
        String authToken2 = authData2.authToken();
        String authToken3 = authData3.authToken();

        gameDAO.createGame(new GameData(1, "username1", "username2", "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, "username1", "username3", "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, "username2", "username3", "game3", new ChessGame()));

        clearService.clear();

        assertNull(userDAO.getUser("username1"));
        assertNull(userDAO.getUser("username2"));
        assertNull(userDAO.getUser("username3"));

        assertThrows(UnauthorizedException.class, () -> authDAO.getAuth(authToken1));
        assertThrows(UnauthorizedException.class, () -> authDAO.getAuth(authToken2));
        assertThrows(UnauthorizedException.class, () -> authDAO.getAuth(authToken3));

        assertNull(gameDAO.getGame(1));
        assertNull(gameDAO.getGame(2));
        assertNull(gameDAO.getGame(3));
    }
}
