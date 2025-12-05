package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;

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
                    makeMove(makeMoveCommand, connection, authToken);
                    break;
                case LEAVE:
                    leave(command, connection, authToken);
                    break;
                case RESIGN:
                    resign(command, connection, authToken);
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

    private void connect(UserGameCommand command, Connection connection, String authToken) {

    }

    private void makeMove(MakeMove command, Connection connection, String authToken) {

    }

    private void leave(UserGameCommand command, Connection connection, String authToken) {

    }

    private void resign(UserGameCommand command, Connection connection, String authToken) {

    }
}
