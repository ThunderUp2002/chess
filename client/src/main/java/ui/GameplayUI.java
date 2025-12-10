package ui;

import chess.*;
import model.GameData;
import websocket.NotificationHandler;
import websocket.WebSocketConnection;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI implements NotificationHandler {
    private ChessGame game;
    private ChessBoard board;
    private ChessGame.TeamColor currentTurn;
    private final boolean isWhitePlayer;
    private boolean isPlaying = true;
    private final String authToken;
    private final GameData gameData;
    private WebSocketConnection webSocketConnection;
    private final static Scanner SCANNER = new Scanner(System.in);
    private Collection<ChessMove> highlightedMoves = new ArrayList<>();
    private ChessPosition highlightedPosition = null;
    private boolean justMadeMove = false;
    private final boolean isObserver;

    public GameplayUI(GameData gameData, boolean isWhitePlayer, boolean isObserver, String authToken, WebSocketConnection webSocketConnection) {
        this.game = gameData.game();
        this.board = gameData.game().getBoard();
        this.currentTurn = gameData.game().getTeamTurn();
        this.isWhitePlayer = isWhitePlayer;
        this.isObserver = isObserver;
        this.authToken = authToken;
        this.gameData = gameData;
        this.webSocketConnection = webSocketConnection;
    }

    public void run() {
        try {
            System.out.print(ERASE_SCREEN);
            sendCommand(UserGameCommand.CommandType.CONNECT);
            Thread.sleep(1000);
            help();

            while (isPlaying) {
                // TODO: Figure out if this correctly addresses the prompt-display problem after troubleshooting move problems
                if (!justMadeMove) {
                    System.out.print(RESET_BG_COLOR);
                    System.out.print(RESET_TEXT_COLOR);
                    System.out.println();
                    printPrompt();
                    justMadeMove = false;
                }
                String input = SCANNER.nextLine().trim().toLowerCase();

                switch (input) {
                    case "help" -> help();
                    case "redraw" -> drawChessBoard();
                    case "highlight" -> highlight();
                    case "move" -> move();
                    case "resign" -> resign();
                    case "leave" -> leave();
                    default -> System.out.println("Unknown command. Type 'help' to see a list of available commands.");
                }
            }

        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error running game: " + e.getMessage());
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    public void help() {
        System.out.println();
        System.out.print(SET_TEXT_COLOR_BLUE + "HELP:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" see a list of possible actions");
        System.out.print(SET_TEXT_COLOR_BLUE + "REDRAW:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" display the current state of the chess board");
        System.out.print(SET_TEXT_COLOR_BLUE + "HIGHLIGHT:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" show legal moves for a piece");
        System.out.print(SET_TEXT_COLOR_BLUE + "MOVE:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" move one of your pieces");
        System.out.print(SET_TEXT_COLOR_BLUE + "RESIGN:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" forfeit the game");
        System.out.print(SET_TEXT_COLOR_BLUE + "LEAVE: ");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" exit the game");
    }

    public void highlight() {
        System.out.print("Enter the position of the piece for which you would like to highlight legal moves (for example, a1): ");
        String input = SCANNER.nextLine().trim().toLowerCase();
        if (isInvalidPosition(input)) {
            System.out.println("Invalid position");
            return;
        }

        // TODO: Figure out why after moving a piece, highlight doesn't update and instead says there is no piece on the space the piece moved to (still highlights where the piece was previously)
        ChessPosition position = constructChessPosition(input);
        ChessPiece piece = gameData.game().getBoard().getPiece(position);
        if (piece == null) {
            System.out.println("No piece on " + input);
            return;
        }

        highlightedMoves = gameData.game().validMoves(position);
        highlightedPosition = position;

        displayBoard();

        highlightedMoves = new ArrayList<>();
        highlightedPosition = null;
    }

    public void move() {
        if (isObserver) {
            System.out.println("Observers cannot make moves");
            return;
        }
        if (isWhitePlayer && currentTurn.equals(ChessGame.TeamColor.BLACK)) {
            System.out.println("You cannot make a move when it is not your turn");
            justMadeMove = false;
            return;
        }
        if (!isWhitePlayer && currentTurn.equals(ChessGame.TeamColor.WHITE)) {
            System.out.println("You cannot make a move when it is not your turn");
            justMadeMove = false;
            return;
        }
        System.out.print("Enter the position of the piece you would like to move (for example, a1): ");
        String startingPosition = SCANNER.nextLine().trim().toLowerCase();
        if (isInvalidPosition(startingPosition)) {
            System.out.println("Invalid position");
            return;
        }
        ChessPosition startPos = constructChessPosition(startingPosition);
        if (gameData.game().getBoard().getPiece(startPos) == null) {
            System.out.println("There is no piece on " + startingPosition);
            return;
        }
        if (isWhitePlayer && gameData.game().getBoard().getPiece(startPos).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            System.out.println("You may not move the other player's pieces");
            return;
        }
        if (!isWhitePlayer && gameData.game().getBoard().getPiece(startPos).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            System.out.println("You may not move the other player's pieces");
            return;
        }
        System.out.print("Enter the position you would like to move this piece (for example, a1): ");
        String endingPosition = SCANNER.nextLine().trim().toLowerCase();
        if (isInvalidPosition(endingPosition)) {
            System.out.println("Invalid position");
            return;
        }
        ChessPosition endPos = constructChessPosition(endingPosition);
        ChessPiece.PieceType type = gameData.game().getBoard().getPiece(startPos).getPieceType();
        ChessMove move;
        boolean isPromotion = (isWhitePlayer && endPos.getRow() == 8 && type == ChessPiece.PieceType.PAWN) ||
                (!isWhitePlayer && endPos.getRow() == 1 && type == ChessPiece.PieceType.PAWN);
        if (isPromotion) {
            System.out.print("Choose a piece to promote your pawn to (BISHOP/KNIGHT/ROOK/QUEEN): ");
            String selection = SCANNER.nextLine().trim().toLowerCase();
            move = new ChessMove(startPos, endPos, selectPromotionPiece(selection));
        }
        else {
            move = new ChessMove(startPos, endPos, null);
        }
        MakeMove makeMove = new MakeMove(authToken, gameData.gameID(), move);
        webSocketConnection.sendCommand(makeMove);
        justMadeMove = true;
    }

    private ChessPiece.PieceType selectPromotionPiece(String selection) {
        return switch (selection) {
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }

    private ChessPosition constructChessPosition(String position) {
        int col = position.charAt(0) - 'a' + 1;
        int row = position.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private boolean isInvalidPosition(String position) {
        return !position.matches("^[a-h][1-8]$");
    }

    public void resign() {
        if (isObserver) {
            System.out.println("Only players can resign");
            return;
        }
        System.out.println("Are you sure you would like to resign? (Y/N)");
        String answer = SCANNER.nextLine().trim().toLowerCase();
        if (answer.equals("y")) {
            sendCommand(UserGameCommand.CommandType.RESIGN);
        }
    }

    public void leave() {
        sendCommand(UserGameCommand.CommandType.LEAVE);
        isPlaying = false;
    }

    private void displayBoard() {
        System.out.print(ERASE_SCREEN);
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println();
        drawChessBoard();
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
    }

    private void drawChessBoard() {
        if (game == null || game.getBoard() == null) {
            System.out.println("Error: game or board not created");
        }

        drawHeader();

        for (int row = 0; row < 8; row++) {
            int displayRank = isWhitePlayer ? (8 - row) : (row + 1);

            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + displayRank + " ");

            drawRow(row);

            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + displayRank + " ");
            System.out.println();
        }

        drawHeader();
    }

    private void drawRow(int row) {
        for (int col = 0; col < 8; col++) {
            int rowNum = isWhitePlayer ? row : (7 - row);
            int colNum = isWhitePlayer ? col : (7 - col);
            boolean isLightSquare = (rowNum + colNum) % 2 == 0;

            ChessPosition position = new ChessPosition(8 - rowNum, colNum + 1);
            if (isPositionHighlighted(position)) {
                if (isLightSquare) {
                    System.out.print(SET_BG_COLOR_GREEN);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_GREEN);
                }
            } else {
                System.out.print(isLightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
            }

            ChessPiece piece = game.getBoard().getPiece(position);
            if (piece == null) {
                System.out.print(EMPTY);
            } else {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(SET_TEXT_COLOR_RED);
                } else {
                    System.out.print(SET_TEXT_COLOR_BLUE);
                }
                String pieceSymbol = getPieceSymbol(piece);
                System.out.print(" " + pieceSymbol + " ");
            }
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    private boolean isPositionHighlighted(ChessPosition position) {
        if (highlightedPosition != null && highlightedPosition.equals(position)) {
            return true;
        }
        return highlightedMoves.stream().anyMatch(move -> move.getEndPosition().equals(position));
    }

    private void drawHeader() {
        System.out.print("   ");
        for (int col = 0; col < 8; col++) {
            int colNum = isWhitePlayer ? col : (7 - col);
            char file = (char) ('a' + colNum);
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + file + " ");
        }
        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                LoadGame loadGame = (LoadGame) serverMessage;
                applyServerGameState(loadGame);
                break;
            case NOTIFICATION:
                Notification notification = (Notification) serverMessage;
                System.out.println();
                System.out.print(SET_TEXT_ITALIC);
                System.out.print(SET_TEXT_COLOR_BLUE);
                System.out.print("NOTIFICATION: ");
                System.out.print(RESET_TEXT_ITALIC);
                System.out.print(RESET_TEXT_COLOR);
                System.out.print(notification.getMessage());
                System.out.println();
                printPrompt();
                break;
            case ERROR:
                Error error = (Error) serverMessage;
                System.out.println();
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println(error.getErrorMessage());
                System.out.print(RESET_TEXT_COLOR);
                printPrompt();
                break;
        }
    }

    private void applyServerGameState(LoadGame loadGame) {
        this.game = loadGame.getGame().game();

        this.board = game.getBoard();
        this.currentTurn = game.getTeamTurn();
        this.highlightedPosition = null;
        this.highlightedMoves.clear();

        displayBoard();
        printPrompt();
    }


    private void sendCommand(UserGameCommand.CommandType type) {
        UserGameCommand command = new UserGameCommand(type, authToken, gameData.gameID());
        webSocketConnection.sendCommand(command);
    }

    public void setWebSocketConnection(WebSocketConnection webSocketConnection) {
        this.webSocketConnection = webSocketConnection;
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_ITALIC + "[GAMEPLAY] >>> ");
        System.out.print(RESET_TEXT_ITALIC);
    }
}
