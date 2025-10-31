package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exceptions.BadRequestException;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import requests.LoginRequest;
import responses.ErrorResponse;
import responses.LoginResponse;
import service.UserService;

public class LoginHandler {
    // TODO: Fix error handling
    public static void handle(Context cxt, UserService userService) throws DataAccessException {
        try {
            Gson gson = new Gson();
            LoginRequest request = gson.fromJson(cxt.body(), LoginRequest.class);
            LoginResponse response = userService.login(request);
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
