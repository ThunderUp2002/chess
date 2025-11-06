package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import responses.ErrorResponse;
import service.UserService;

public class LogoutHandler {
    public static void handle(Context cxt, UserService userService) {
        try {
            String authToken = cxt.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            userService.logout(authToken);
            cxt.status(200).result("{}");
        } catch (UnauthorizedException e) {
            cxt.status(401).result(new Gson().toJson(new ErrorResponse("Error: unauthorized")));
        } catch (Exception e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
