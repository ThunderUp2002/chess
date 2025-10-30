package handler;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import requests.CreateGameRequest;
import responses.CreateGameResponse;
import responses.ErrorResponse;
import service.GameService;

public class CreateGameHandler {

    public static void handle(Context cxt, GameService gameService) throws Exception {
        try {
            Gson gson = new Gson();
            CreateGameRequest request = gson.fromJson(cxt.body(), CreateGameRequest.class);
            CreateGameResponse response = gameService.createGame(request);
            cxt.status(200);
            cxt.result(gson.toJson(response));
        } catch (BadRequestException e) {
            cxt.status(400).result(new Gson().toJson(new ErrorResponse("Error: bad request")));
        } catch (UnauthorizedException e) {
            cxt.status(401).result(new Gson().toJson(new ErrorResponse("Error: unauthorized")));
        } catch (GeneralException e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
