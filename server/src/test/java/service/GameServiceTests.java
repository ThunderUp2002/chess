package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import responses.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        gameDAO = new DatabaseGameDAO();
        userDAO = new DatabaseUserDAO();
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        authDAO.deleteAuths();
        gameDAO.deleteGames();
        userDAO.deleteUsers();
    }

    @Test
    public void createGameSuccess() throws Exception {
        RegisterResponse response = userService.register(new RegisterRequest("username", "password", "email"));
        String authToken = response.authToken();
        assertNotNull(gameService.createGame(new CreateGameRequest("game1"), authToken));
    }

    @Test
    public void createGameFailure() {
        assertThrows(BadRequestException.class, () -> gameService.createGame(new CreateGameRequest(null), "abc"));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        userDAO.createUser(new UserData("username1", "password1", "email1@gmail.com"));
        userDAO.createUser(new UserData("username2", "password2", "email2@gmail.com"));
        userDAO.createUser(new UserData("username3", "password3", "email3@gmail.com"));

        AuthData authData1 = authDAO.createAuth("username1");
        String authToken1 = authData1.authToken();

        gameDAO.createGame(new GameData(1, "username1", "username2", "game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, "username1", "username3", "game2", new ChessGame()));
        gameDAO.createGame(new GameData(3, "username2", "username3", "game3", new ChessGame()));

        assertFalse(gameService.listGames(authToken1).isEmpty());
    }

    @Test
    public void listGamesFailure() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("abc"));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        userDAO.createUser(new UserData("username1", "password1", "email1@gmail.com"));
        AuthData authData1 = authDAO.createAuth("username1");
        String authToken1 = authData1.authToken();
        gameDAO.createGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameService.joinGame(new JoinGameRequest("white", 1), authToken1);
        GameData gameData = gameDAO.getGame(1);
        assertEquals("username1", gameData.whiteUsername());
    }

    @Test
    public void joinGameFailure() {
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(new JoinGameRequest("white", 1), "abc"));
    }
}
