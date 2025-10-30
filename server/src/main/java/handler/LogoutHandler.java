package handler;

import com.google.gson.Gson;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;
import responses.ErrorResponse;
import service.UserService;

public class LogoutHandler {
    // TODO: Fix error handling
    public static void handle(Context cxt, UserService userService) throws Exception {
        try {
            String authToken = cxt.header("authorization");
            userService.logout(authToken);
            cxt.status(200);
            cxt.result("{}");
        } catch (UnauthorizedException e) {
            cxt.status(401).result(new Gson().toJson(new ErrorResponse("Error: unauthorized")));
        } catch (GeneralException e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
