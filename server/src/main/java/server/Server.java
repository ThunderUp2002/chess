package server;

import io.javalin.*;
import io.javalin.http.Context;
import handler.*;
import service.*;
import dataaccess.*;

public class Server {

    private final Javalin javalin;

    private ClearService clearService;
    private GameService gameService;
    private UserService userService;

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
        try {
            AuthDAO authDAO = new DatabaseAuthDAO();
            GameDAO gameDAO = new DatabaseGameDAO();
            UserDAO userDAO = new DatabaseUserDAO();

            this.clearService = new ClearService(authDAO, gameDAO, userDAO);
            this.gameService = new GameService(authDAO, gameDAO);
            this.userService = new UserService(authDAO, userDAO);

            javalin.start(desiredPort);
            return javalin.port();
        } catch (DataAccessException e) {
            stop();
            return -1;
        }
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context cxt) {
        RegisterHandler.handle(cxt, userService);
    }

    private void login(Context cxt) {
        LoginHandler.handle(cxt, userService);
    }

    private void logout(Context cxt) {
        LogoutHandler.handle(cxt, userService);
    }

    private void listGames(Context cxt) {
        ListGamesHandler.handle(cxt, gameService);
    }

    private void createGame(Context cxt) {
        CreateGameHandler.handle(cxt, gameService);
    }

    private void joinGame(Context cxt) {
        JoinGameHandler.handle(cxt, gameService);
    }

    private void clear(Context cxt) {
        ClearHandler.handle(cxt, clearService);
    }
}
