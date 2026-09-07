package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        // Create a new (empty) chess board
    }

    private int[] interpretChessPosition(ChessPosition position) {
        return new int[] {position.getRow() - 1, position.getColumn() - 1};
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        final int[] posData = interpretChessPosition(position);
        final int r = posData[0];
        final int c = posData[1];
        board[r][c] = piece;
    }


    private ChessPiece[] buildHomeRow(ChessGame.TeamColor color) {
        return new ChessPiece[] {
                new ChessPiece(color, ChessPiece.PieceType.ROOK),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.QUEEN),
                new ChessPiece(color, ChessPiece.PieceType.KING),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.ROOK),
        };
    }

    private ChessPiece[] buildPawnRow(ChessGame.TeamColor color) {
        final ChessPiece[] row = new ChessPiece[8];
        for (int i = 0; i < 8; i++) {
            row[i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
        return row;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        final int[] posData = interpretChessPosition(position);
        final int r = posData[0];
        final int c = posData[1];
        return board[r][c];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board[0] = buildHomeRow(ChessGame.TeamColor.WHITE);
        this.board[1] = buildPawnRow(ChessGame.TeamColor.WHITE);
        this.board[2] = new ChessPiece[8];
        this.board[3] = new ChessPiece[8];
        this.board[4] = new ChessPiece[8];
        this.board[5] = new ChessPiece[8];
        this.board[6] = buildPawnRow(ChessGame.TeamColor.BLACK);
        this.board[7] = buildHomeRow(ChessGame.TeamColor.BLACK);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder('\n');
        for (ChessPiece[] row : board) {
            for (ChessPiece p : row) {
                if (p == null) {
                    sb.append(" -- ");
                    continue;
                }
                String color = (p.getTeamColor() == ChessGame.TeamColor.WHITE) ? "W" : "B";
                switch (p.getPieceType()) {
                    case ChessPiece.PieceType.PAWN -> sb.append(" %sP ".formatted(color));
                    case ChessPiece.PieceType.ROOK -> sb.append(" %sR ".formatted(color));
                    case ChessPiece.PieceType.KNIGHT -> sb.append(" %sH ".formatted(color));
                    case ChessPiece.PieceType.BISHOP -> sb.append(" %sB ".formatted(color));
                    case ChessPiece.PieceType.QUEEN -> sb.append(" %sQ ".formatted(color));
                    case ChessPiece.PieceType.KING -> sb.append(" %sK ".formatted(color));
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
