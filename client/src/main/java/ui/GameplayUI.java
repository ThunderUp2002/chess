package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import websocket.NotificationHandler;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI implements NotificationHandler {
    private ChessGame game;
    private final boolean isWhitePlayer;
    private boolean isPlaying = true;

    public GameplayUI(GameData gameData, boolean isWhitePlayer) {
        this.game = gameData.game();
        this.isWhitePlayer = isWhitePlayer;
    }

    public void run() {
        try {
            System.out.print(ERASE_SCREEN);
            System.out.println("Joining game...");
            displayBoard();

            Scanner scanner = new Scanner(System.in);

            while (isPlaying) {
                System.out.print(RESET_BG_COLOR);
                System.out.print(RESET_TEXT_COLOR);
                System.out.println();
                System.out.print(SET_TEXT_ITALIC + "[GAMEPLAY] >>> ");
                System.out.print(RESET_TEXT_ITALIC);
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "help" -> help();
                    case "redraw" -> drawChessBoard();
                    case "highlight" -> highlight();
                    case "move" -> move();
                    case "resign" -> resign();
                    case "leave" -> leave();  // TODO: Ensure leave returns the user to PostLoginUI
                    default -> System.out.println("Unknown command. Type 'help' to see a list of available commands.");
                }
            }

        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error running game");
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

    }

    public void move() {

    }

    public void resign() {

    }

    public void leave() {
        // TODO: Ensure leave returns the user to PostLoginUI
        System.out.println("Leaving game...");
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
            System.out.print(isLightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);

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
                this.game = loadGame.getGame().game();
                displayBoard();
                break;
            case NOTIFICATION:
                Notification notification = (Notification) serverMessage;
                System.out.println();
                System.out.println(notification.getMessage());
                System.out.println();
                break;
            case ERROR:
                Error error = (Error) serverMessage;
                System.out.println();
                System.out.println(error.getErrorMessage());
                System.out.println();
                break;
        }
    }
}
