package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String authToken = command.getAuthToken();
            var connection = connectionManager.getConnection(authToken);

            if (connection == null) {
                if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                    Integer gameID = command.getGameID();
                    var session = ctx.session;
                    connection = connectionManager.add(gameID, authToken, session);
                }
            }

            switch (command.getCommandType()) {
                case CONNECT:
                    connect(command, connection, authToken);
                    break;
                case MAKE_MOVE:
                    MakeMove makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMove.class);
                    makeMove(makeMoveCommand, authToken);
                    break;
                case LEAVE:
                    leave(command, authToken);
                    break;
                case RESIGN:
                    resign(command, authToken);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Connection.sendError(ctx.session, e.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Connection connection, String authToken) throws Exception {
        String username = authDAO.getAuth(authToken).username();
        Integer gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception ("Game does not exist");
        }
        String role = determineRole(gameData, username);
        Notification notification = new Notification(String.format("%s joined as %s", username, role));
        String notificationJSON = new Gson().toJson(notification);
        LoadGame loadGame = new LoadGame(gameData);
        String loadGameJSON = new Gson().toJson(loadGame);
        connection.send(loadGameJSON);
        connectionManager.broadcastExclusion(gameID, authToken, notificationJSON);
    }

    private String determineRole(GameData gameData, String username) {
        if (username.equals(gameData.whiteUsername())) {
            return "white";
        }
        if (username.equals(gameData.blackUsername())) {
            return "black";
        }
        else {
            return "observer";
        }
    }

    private void makeMove(MakeMove command, String authToken) {

    }

    private void leave(UserGameCommand command, String authToken) throws Exception {
        String username = authDAO.getAuth(authToken).username();
        Integer gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        GameData updatedGameData = gameData;
        if (gameData == null) {
            throw new Exception("Game does not exist");
        }
        if (username.equals(gameData.whiteUsername())) {
            updatedGameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        if (username.equals(gameData.blackUsername())) {
            updatedGameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }
        gameDAO.updateGame(updatedGameData);
        connectionManager.remove(gameID, authToken);

        Notification notification = new Notification(String.format("%s has left the game", username));
        String notificationJSON = new Gson().toJson(notification);
        connectionManager.broadcastExclusion(gameID, authToken, notificationJSON);
    }

    private void resign(UserGameCommand command, String authToken) throws Exception {
        String username = authDAO.getAuth(authToken).username();
        Integer gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Game does not exist");
        }
        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            throw new Exception("Game has already ended");
        }
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            throw new Exception("Only players can resign");
        }
        game.endGame();
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(updatedGameData);

        Notification notification = new Notification(String.format("%s has resigned from the game", username));
        String notificationJSON = new Gson().toJson(notification);
        connectionManager.broadcastAll(gameID, notificationJSON);
    }
}
