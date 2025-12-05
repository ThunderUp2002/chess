package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Error;

import java.io.IOException;

public class Connection {
    private Session session;
    private String authToken;

    public Connection(Session session, String authToken) {
        this.session = session;
        this.authToken = authToken;
    }

    public static void sendError(Session session, String message) {
        try {
            String completeErrorMessage = "Error: " + message;
            Error error = new Error(completeErrorMessage);
            String errorJSON = new Gson().toJson(error);
            session.getRemote().sendString(errorJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
