package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketConnection extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketConnection(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
                    switch (serverMessageType) {
                        case NOTIFICATION:
                            Notification notification = new Gson().fromJson(message, Notification.class);
                            notificationHandler.notify(notification);
                            break;
                        case ERROR:
                            Error error = new Gson().fromJson(message, Error.class);
                            notificationHandler.notify(error);
                            break;
                        case LOAD_GAME:
                            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                            notificationHandler.notify(loadGame);
                            break;
                        default:
                            break;
                    }
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (Exception e) {
            System.err.println("Error handling server message: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Connected to WebSocket");
        this.session = session;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason.getReasonPhrase());
        this.session = null;
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    public void sendCommand(UserGameCommand command) {
        try {
            if (session.isOpen() && session != null) {
                String commandJSON = new Gson().toJson(command);
                session.getBasicRemote().sendText(commandJSON);
            }
        } catch (IOException e) {
            System.err.println("Error sending command: " + e.getMessage());
        }
    }
}
