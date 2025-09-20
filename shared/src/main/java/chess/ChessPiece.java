package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING:
                return calculateKingMoves(board, myPosition);
//            case QUEEN:
//                return calculateQueenMoves(board, myPosition);
            case BISHOP:
                return calculateBishopMoves(board, myPosition);
            case KNIGHT:
                return calculateKnightMoves(board, myPosition);
            case ROOK:
                return calculateRookMoves(board, myPosition);
//            case PAWN:
//                return calculatePawnMoves(board, myPosition);
            default:
                return new ArrayList<>();
        }
    }

    private boolean inBounds(int row, int col) {
        return row > 0 && row <= 8  && col > 0 && col <= 8;
    }

    public void checkShortMove(ChessBoard board, ChessPosition futurePosition, int row, int col, Collection<ChessMove> possibleMoves) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            possibleMoves.add(new ChessMove(futurePosition, new ChessPosition(row, col), null));
        }
        else if (piece.getTeamColor() != this.getTeamColor()) {
            possibleMoves.add(new ChessMove(futurePosition, new ChessPosition(row, col), null));
        }
    }

    public void checkLongMove(ChessBoard board, ChessPosition futurePosition, int row, int col, int horizontal, int vertical, Collection<ChessMove> possibleMoves) {
        while (inBounds(row, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece == null) {
                possibleMoves.add(new ChessMove(futurePosition, new ChessPosition(row, col), null));
                row = row + horizontal;
                col = col + vertical;
            }
            else if (piece.getTeamColor() != this.getTeamColor()) {
                possibleMoves.add(new ChessMove(futurePosition, new ChessPosition(row, col), null));
                break;
            }
            else {
                break;
            }
        }
    }

    private Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (inBounds(row + 1, col)) {
            checkShortMove(board, myPosition, row + 1, col, possibleMoves);
        }
        if (inBounds(row + 1, col + 1)) {
            checkShortMove(board, myPosition, row + 1, col + 1, possibleMoves);
        }
        if (inBounds(row, col + 1)) {
            checkShortMove(board, myPosition, row, col + 1, possibleMoves);
        }
        if (inBounds(row - 1, col + 1)) {
            checkShortMove(board, myPosition, row - 1, col + 1, possibleMoves);
        }
        if (inBounds(row - 1, col)) {
            checkShortMove(board, myPosition, row - 1, col, possibleMoves);
        }
        if (inBounds(row - 1, col - 1)) {
            checkShortMove(board, myPosition, row - 1, col - 1, possibleMoves);
        }
        if (inBounds(row, col - 1)) {
            checkShortMove(board, myPosition, row, col - 1, possibleMoves);
        }
        if (inBounds(row + 1, col - 1)) {
            checkShortMove(board, myPosition, row + 1, col - 1, possibleMoves);
        }

        return possibleMoves;
    }

    private Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (inBounds(row + 2, col + 1)) {
            checkShortMove(board, myPosition, row + 2, col + 1, possibleMoves);
        }
        if (inBounds(row + 1, col + 2)) {
            checkShortMove(board, myPosition, row + 1, col + 2, possibleMoves);
        }
        if (inBounds(row - 1, col + 2)) {
            checkShortMove(board, myPosition, row - 1, col + 2, possibleMoves);
        }
        if (inBounds(row - 2, col + 1)) {
            checkShortMove(board, myPosition, row - 2, col + 1, possibleMoves);
        }
        if (inBounds(row - 2, col - 1)) {
            checkShortMove(board, myPosition, row - 2, col - 1, possibleMoves);
        }
        if (inBounds(row - 1, col - 2)) {
            checkShortMove(board, myPosition, row - 1, col - 2, possibleMoves);
        }
        if (inBounds(row + 1, col - 2)) {
            checkShortMove(board, myPosition, row + 1, col - 2, possibleMoves);
        }
        if (inBounds(row + 2, col - 1)) {
            checkShortMove(board, myPosition, row + 2, col - 1, possibleMoves);
        }

        return possibleMoves;
    }

    private Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int horizontal = 1;
        int vertical = 1;
        int negHorizontal = -1;
        int negVertical = -1;

        checkLongMove(board, myPosition, row + 1, col + 1, horizontal, vertical, possibleMoves);
        checkLongMove(board, myPosition, row + 1, col - 1, horizontal, negVertical, possibleMoves);
        checkLongMove(board, myPosition, row - 1, col + 1, negHorizontal, vertical, possibleMoves);
        checkLongMove(board, myPosition, row - 1, col - 1, negHorizontal, negVertical, possibleMoves);

        return possibleMoves;
    }

    private Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int horizontal = 1;
        int vertical = 1;
        int negHorizontal = -1;
        int negVertical = -1;

        checkLongMove(board, myPosition, row + 1, col, horizontal, 0, possibleMoves);
        checkLongMove(board, myPosition, row - 1, col, negHorizontal, 0, possibleMoves);
        checkLongMove(board, myPosition, row, col + 1, 0, vertical, possibleMoves);
        checkLongMove(board, myPosition, row, col - 1, 0, negVertical, possibleMoves);

        return possibleMoves;
    }
}
