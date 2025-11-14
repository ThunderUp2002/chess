package client;

import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws Exception {
        var authData = facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("username", "password", "test@gmail.com")));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        var authData = facade.login(new LoginRequest("username", "password"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("username", "wrongPassword")));
    }

    @Test
    public void logoutPositive() throws Exception {
        var authData = facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        String authToken = authData.authToken();
        assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    public void logoutNegative() {
        assertThrows(Exception.class, () -> facade.logout("abc"));
    }

    @Test
    public void createGamePositive() throws Exception {
        var authData = facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        String authToken = authData.authToken();
        var gameData = facade.createGame(new CreateGameRequest("game1"), authToken);
        assertNotNull(gameData);
    }

    @Test
    public void createGameNegative() {
        assertThrows(Exception.class, () -> facade.createGame(null, "abc"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        var authData = facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        String authToken = authData.authToken();
        var gamesList = facade.listGames(authToken);
        assertNotNull(gamesList);
    }

    @Test
    public void listGamesNegative() {
        assertThrows(Exception.class, () -> facade.listGames("abc"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        var authData = facade.register(new RegisterRequest("username", "password", "test@gmail.com"));
        String authToken = authData.authToken();
        var gameData = facade.createGame(new CreateGameRequest("game1"), authToken);
        int gameID = gameData.gameID();
        assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest("white", gameID), authToken));
    }

    @Test
    public void joinGameNegative() {
        assertThrows(Exception.class, () -> facade.joinGame(null, "abc"));
    }
}
