package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exceptions.GeneralException;
import responses.ErrorResponse;
import service.ClearService;
import io.javalin.http.Context;

public class ClearHandler {

    public static void handle(Context cxt, ClearService clearService) {
        try {
            clearService.clear();
            cxt.status(200).result("{}");
        } catch (Exception e) {
            cxt.status(500).result(new Gson().toJson(new ErrorResponse("Error: something went wrong")));
        }
    }
}
