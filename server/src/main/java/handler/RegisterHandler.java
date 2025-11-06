package handler;

import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import responses.ErrorResponse;
import service.UserService;
import io.javalin.http.Context;
import requests.RegisterRequest;
import responses.RegisterResponse;

public class RegisterHandler {

    public static void handle(Context cxt, UserService userService) {
        try {
            Gson gson = new Gson();
            RegisterRequest request = gson.fromJson(cxt.body(), RegisterRequest.class);
            RegisterResponse response = userService.register(request);
            cxt.status(200);
            cxt.result(gson.toJson(response));
        } catch (BadRequestException e) {
            cxt.status(400).result(new Gson().toJson(new ErrorResponse("Error: bad request")));
        } catch (AlreadyTakenException e) {
            cxt.status(403).result(new Gson().toJson(new ErrorResponse("Error: already taken")));
        } catch (Exception e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
