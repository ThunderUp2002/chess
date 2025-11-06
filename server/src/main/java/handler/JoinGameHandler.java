package handler;

import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import requests.JoinGameRequest;
import responses.ErrorResponse;
import service.GameService;

public class JoinGameHandler {

    public static void handle(Context cxt, GameService gameService) {
        try {
            String authToken = cxt.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            Gson gson = new Gson();
            JoinGameRequest request = gson.fromJson(cxt.body(), JoinGameRequest.class);
            gameService.joinGame(request, authToken);
            cxt.status(200).result("{}");
        } catch (BadRequestException e) {
            cxt.status(400).result(new Gson().toJson(new ErrorResponse("Error: bad request")));
        } catch (UnauthorizedException e) {
            cxt.status(401).result(new Gson().toJson(new ErrorResponse("Error: unauthorized")));
        } catch (AlreadyTakenException e) {
            cxt.status(403).result(new Gson().toJson(new ErrorResponse("Error: already taken")));
        } catch (Exception e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
