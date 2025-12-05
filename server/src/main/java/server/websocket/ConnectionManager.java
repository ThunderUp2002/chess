package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, CopyOnWriteArraySet<Connection>> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Connection> connectionByAuthToken = new ConcurrentHashMap<>();

    public Connection add(Integer gameID, String authToken, Session session) {
        Connection connection = new Connection(session, authToken);

        connections.computeIfAbsent(gameID, k -> new CopyOnWriteArraySet<>()).add(connection);
        connectionByAuthToken.put(authToken, connection);

        return connection;
    }

    public void remove(Integer gameID, String authToken) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.removeIf(connection -> connection.getAuthToken().equals(authToken));
        }
        connectionByAuthToken.remove(authToken);
    }

    public void broadcastExclusion(Integer gameID, String excludedAuthToken, String message) throws IOException {
        for (var c : connections.get(gameID)) {
            if (c.getSession().isOpen()) {
                if(!c.getAuthToken().equals(excludedAuthToken)) {
                    c.send(message);
                }
            }
        }
    }

    public void broadcastAll(Integer gameID, String message) throws IOException {
        for (var c : connections.get(gameID)) {
            if (c.getSession().isOpen()) {
                c.send(message);
            }
        }
    }

    public Connection getConnection(String authToken) {
        return connectionByAuthToken.get(authToken);
    }
}
