package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

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

    private void register(Context cxt) {

    }

    private void login(Context cxt) {

    }

    private void logout(Context cxt) {

    }

    private void listGames(Context cxt) {

    }

    private void createGame(Context cxt) {

    }

    private void joinGame(Context cxt) {

    }

    private void clear(Context cxt) {

    }
}
