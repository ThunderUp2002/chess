package server;

import com.google.gson.Gson;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import websocket.NotificationHandler;
import websocket.WebSocketConnection;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.Collections;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public WebSocketConnection initWebSocket(NotificationHandler handler) {
        return new WebSocketConnection(serverUrl, handler);
    }

    public void clear() throws Exception {
        var httpRequest = buildRequest("DELETE", "/db", null, null);
        sendRequest(httpRequest);
    }

    public RegisterResponse register(RegisterRequest request) throws Exception {
        var httpRequest = buildRequest("POST", "/user", request, null);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) throws Exception {
        var httpRequest = buildRequest("POST", "/session", request, null);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, LoginResponse.class);
    }

    public void logout(String authToken) throws Exception {
        var httpRequest = buildRequest("DELETE", "/session", null, authToken);
        var httpResponse = sendRequest(httpRequest);
        handleResponse(httpResponse, null);
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws Exception {
        var httpRequest = buildRequest("POST", "/game", request, authToken);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, CreateGameResponse.class);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        var httpRequest = buildRequest("GET", "/game", null, authToken);
        var httpResponse = sendRequest(httpRequest);
        var result = handleResponse(httpResponse, ListGamesResponse.class);
        if (result != null) {
            return result.games();
        }
        else {
            return Collections.emptyList();
        }
    }

    public void joinGame(JoinGameRequest request, String authToken) throws Exception {
        var httpRequest = buildRequest("PUT", "/game", request, authToken);
        var httpResponse = sendRequest(httpRequest);
        handleResponse(httpResponse, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Error sending request", ex);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new Exception(String.format("Error: %s", status));
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
