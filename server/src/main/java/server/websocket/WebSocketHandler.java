package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.util.Collection;

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
        System.out.println("WebSocket connected");
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
        System.out.println("WebSocket closed");
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

    private void makeMove(MakeMove command, String authToken) throws Exception {
        String username = authDAO.getAuth(authToken).username();
        Integer gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Game does not exist");
        }

        ChessGame game = gameData.game();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());
        if (isWhite && game.getTeamTurn().equals(ChessGame.TeamColor.BLACK)) {
            throw new Exception("You cannot make a move when it is not your turn");
        }
        if (isBlack && game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
            throw new Exception("You cannot make a move when it is not your turn");
        }
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            throw new Exception("Observers cannot make moves");
        }
        if (game.isGameOver()) {
            throw new Exception("The game has already ended");
        }
        Collection<ChessMove> validMoves = game.validMoves(command.getMove().getStartPosition());
        if (!validMoves.contains(command.getMove())) {
            throw new Exception("That move is not valid");
        }
        ChessPosition position = command.getMove().getStartPosition();
        ChessPiece piece = game.getBoard().getPiece(position);
        if (piece == null) {
            throw new Exception (String.format("There is no piece on %s", formatPosition(position)));
        }
        boolean isWrongTeam = (isWhite && (game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor()).equals(ChessGame.TeamColor.BLACK)) ||
                (isBlack && (game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor()).equals(ChessGame.TeamColor.WHITE));
        if (isWrongTeam) {
            throw new Exception("You may not move the other player's pieces");
        }

        game.makeMove(command.getMove());
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(updatedGameData);

        LoadGame loadGame = new LoadGame(updatedGameData);
        String loadGameJSON = new Gson().toJson(loadGame);
        connectionManager.broadcastAll(gameID, loadGameJSON);

        String pieceType = convertPieceType(game.getBoard().getPiece(command.getMove().getEndPosition()).getPieceType());
        String formattedStartPos = formatPosition(command.getMove().getStartPosition());
        String formattedEndPos = formatPosition(command.getMove().getEndPosition());
        Notification moveNotification = new Notification(String.format("%s moved their %s from %s to %s", username, pieceType, formattedStartPos, formattedEndPos));
        String moveNotificationJSON = new Gson().toJson(moveNotification);
        connectionManager.broadcastExclusion(gameID, authToken, moveNotificationJSON);

        if (game.isInCheck(ChessGame.TeamColor.WHITE) && !game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            Notification checkNotification = new Notification(String.format("%s is in check", gameData.whiteUsername()));
            String checkNotificationJSON = new Gson().toJson(checkNotification);
            connectionManager.broadcastAll(gameID, checkNotificationJSON);
        }
        if (game.isInCheck(ChessGame.TeamColor.BLACK) && !game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            Notification checkNotification = new Notification(String.format("%s is in check", gameData.blackUsername()));
            String checkNotificationJSON = new Gson().toJson(checkNotification);
            connectionManager.broadcastAll(gameID, checkNotificationJSON);
        }
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            endGame(gameData);
            Notification checkmateNotification = new Notification(String.format("%s is in checkmate. %s has won the game!", gameData.whiteUsername(), gameData.blackUsername()));
            String checkmateNotificationJSON = new Gson().toJson(checkmateNotification);
            connectionManager.broadcastAll(gameID, checkmateNotificationJSON);
        }
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            endGame(gameData);
            Notification checkmateNotification = new Notification(String.format("%s is in checkmate. %s has won the game!", gameData.blackUsername(), gameData.whiteUsername()));
            String checkmateNotificationJSON = new Gson().toJson(checkmateNotification);
            connectionManager.broadcastAll(gameID, checkmateNotificationJSON);
        }
        if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            endGame(gameData);
            Notification stalemateNotification = new Notification(String.format("%s has no remaining moves. The game has ended in a stalemate.", gameData.whiteUsername()));
            String stalemateNotificationJSON = new Gson().toJson(stalemateNotification);
            connectionManager.broadcastAll(gameID, stalemateNotificationJSON);
        }
        if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            endGame(gameData);
            Notification stalemateNotification = new Notification(String.format("%s has no remaining moves. The game has ended in a stalemate.", gameData.blackUsername()));
            String stalemateNotificationJSON = new Gson().toJson(stalemateNotification);
            connectionManager.broadcastAll(gameID, stalemateNotificationJSON);
        }
    }

    private String convertPieceType(ChessPiece.PieceType type) {
        return switch (type) {
            case ROOK -> "rook";
            case BISHOP -> "bishop";
            case QUEEN -> "queen";
            case KNIGHT -> "knight";
            case KING -> "king";
            case PAWN -> "pawn";
        };
    }

    private String formatPosition(ChessPosition position) {
        char col = (char) ('a' + position.getColumn() - 1);
        int row = position.getRow();
        return String.format("%c%d", col, row);
    }

    private void endGame(GameData gameData) throws Exception {
        gameData.game().endGame();
        GameData endedGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        gameDAO.updateGame(endedGame);
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
