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
        return switch (type) {
            case KING -> calculateKingMoves(board, myPosition);
            case QUEEN -> calculateQueenMoves(board, myPosition);
            case BISHOP -> calculateBishopMoves(board, myPosition);
            case KNIGHT -> calculateKnightMoves(board, myPosition);
            case ROOK -> calculateRookMoves(board, myPosition);
            case PAWN -> calculatePawnMoves(board, myPosition);
        };
    }

    private boolean inBounds(int row, int col) {
        return row > 0 && row <= 8  && col > 0 && col <= 8;
    }

    public void checkShortMove(ChessBoard board, ChessPosition currentPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            possibleMoves.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
        }
        else if (piece.getTeamColor() != this.getTeamColor()) {
            possibleMoves.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
        }
    }

    public void checkLongMove(ChessBoard board, ChessPosition currentPosition, int row, int col, int horizontal, int vertical, Collection<ChessMove> possibleMoves) {
        while (inBounds(row, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece == null) {
                possibleMoves.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
                row = row + horizontal;
                col = col + vertical;
            }
            else if (piece.getTeamColor() != this.getTeamColor()) {
                possibleMoves.add(new ChessMove(currentPosition, new ChessPosition(row, col), null));
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

    private Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(calculateBishopMoves(board, myPosition));
        possibleMoves.addAll(calculateRookMoves(board, myPosition));
        return possibleMoves;
    }

    public void moveWhitePawn(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        defaultWhiteMove(board, myPosition, row, col, possibleMoves);
        attackWhiteMove(board, myPosition, row + 1, col + 1, possibleMoves);
        attackWhiteMove(board, myPosition, row + 1, col - 1, possibleMoves);
        firstWhiteMove(board, myPosition, row, col, possibleMoves);
    }

    public void defaultWhiteMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (inBounds(row + 1, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col));
            if (piece == null && row == 7) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.QUEEN));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.ROOK));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.BISHOP));
            }
            else if (piece == null) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
            }
        }
    }

    public void attackWhiteMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (inBounds(row, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece != null) {
                if (piece.getTeamColor() != this.getTeamColor() && row == 8) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.BISHOP));
                }
                else if (piece.getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }
        }
    }

    public void firstWhiteMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (row == 2) {
            if (inBounds(row + 2, col)) {
                ChessPiece frontPiece = board.getPiece(new ChessPosition(row + 1, col));
                ChessPiece piece = board.getPiece(new ChessPosition(row + 2, col));
                if (frontPiece == null && piece == null) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
                }
            }
        }
    }

    public void moveBlackPawn(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        defaultBlackMove(board, myPosition, row, col, possibleMoves);
        attackBlackMove(board, myPosition, row - 1, col + 1, possibleMoves);
        attackBlackMove(board, myPosition, row - 1, col - 1, possibleMoves);
        firstBlackMove(board, myPosition, row, col, possibleMoves);
    }

    public void defaultBlackMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (inBounds(row - 1, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row - 1, col));
            if (piece == null && row == 2) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), PieceType.QUEEN));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), PieceType.ROOK));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), PieceType.BISHOP));
            }
            else if (piece == null) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
            }
        }
    }

    public void attackBlackMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (inBounds(row, col)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece != null) {
                if (piece.getTeamColor() != this.getTeamColor() && row == 1) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.BISHOP));
                }
                else if (piece.getTeamColor() != this.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }
        }
    }

    public void firstBlackMove(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> possibleMoves) {
        if (row == 7) {
            if (inBounds(row - 2, col)) {
                ChessPiece frontPiece = board.getPiece(new ChessPosition(row - 1, col));
                ChessPiece piece = board.getPiece(new ChessPosition(row - 2, col));
                if (frontPiece == null && piece == null) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col), null));
                }
            }
        }
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ArrayList<ChessMove> possibleMoves = new ArrayList<>();
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            moveWhitePawn(board, myPosition, row, col, possibleMoves);
            return possibleMoves;
        }
        else {
            ArrayList<ChessMove> possibleMoves = new ArrayList<>();
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            moveBlackPawn(board, myPosition, row, col, possibleMoves);
            return possibleMoves;
        }
    }
}
