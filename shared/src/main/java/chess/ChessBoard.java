package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board[0] = buildHomeRow(ChessGame.TeamColor.BLACK);
        this.board[1] = buildPawnRow(ChessGame.TeamColor.BLACK);
        this.board[2] = new ChessPiece[8];
        this.board[3] = new ChessPiece[8];
        this.board[4] = new ChessPiece[8];
        this.board[5] = new ChessPiece[8];
        this.board[6] = buildPawnRow(ChessGame.TeamColor.WHITE);
        this.board[7] = buildHomeRow(ChessGame.TeamColor.WHITE);
    }
}
