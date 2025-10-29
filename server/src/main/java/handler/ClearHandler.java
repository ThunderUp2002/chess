package handler;

import dataaccess.DataAccessException;
import service.ClearService;
import io.javalin.http.Context;

public class ClearHandler {

    public static void handle(Context cxt, ClearService clearService) throws DataAccessException {
        clearService.clear();
        cxt.status(200).result("{}");
    }
}
