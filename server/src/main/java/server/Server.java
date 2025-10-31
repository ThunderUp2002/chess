package server;

import io.javalin.*;
import io.javalin.http.Context;
import handler.*;
import service.*;
import dataaccess.*;

public class Server {

    private final Javalin javalin;

    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();

    private final ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);
    private final UserService userService = new UserService(authDAO, userDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context cxt) throws Exception {
        RegisterHandler.handle(cxt, userService);
    }

    private void login(Context cxt) throws Exception {
        LoginHandler.handle(cxt, userService);
    }

    private void logout(Context cxt) throws Exception {
        LogoutHandler.handle(cxt, userService);
    }

    private void listGames(Context cxt) throws Exception {
        ListGamesHandler.handle(cxt, gameService);
    }

    private void createGame(Context cxt) throws Exception {
        CreateGameHandler.handle(cxt, gameService);
    }

    private void joinGame(Context cxt) throws Exception {
        JoinGameHandler.handle(cxt, gameService);
    }

    private void clear(Context cxt) throws DataAccessException {
        ClearHandler.handle(cxt, clearService);
    }
}
