package handler;

import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import model.GameData;
import responses.ErrorResponse;
import responses.ListGamesResponse;
import service.GameService;

import java.util.Collection;

public class ListGamesHandler {
    public static void handle(Context cxt, GameService gameService) {
        try {
            Gson gson = new Gson();
            String authToken = cxt.header("authorization");
            Collection<GameData> games = gameService.listGames(authToken);
            cxt.status(200).result(gson.toJson(new ListGamesResponse(games)));
        } catch (UnauthorizedException e) {
            cxt.status(401).result(new Gson().toJson(new ErrorResponse("Error: unauthorized")));
        } catch (Exception e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
