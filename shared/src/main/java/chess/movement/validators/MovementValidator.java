package chess.movement.validators;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessPiece;

public interface MovementValidator {
    // Performs the basic checks of out-of-bounds and overlapping your own team
    static boolean checkBasic(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        final int[] posData = ChessBoard.interpretChessPosition(move.getEndPosition());
        final int targetRow = posData[0];
        final int targetCol = posData[1];

        // Check out of bounds
        if ((targetRow < 0 || targetCol < 0) || (targetRow >= b.getSize() || targetCol >= b.getSize())) {
            return false;
        }
        // Check capturing
        ChessPiece targetPiece = b.getPiece(move.getEndPosition());
        boolean targetOccupied = targetPiece != null;
        if (targetOccupied) {
            return targetPiece.getTeamColor() != color;
        }
        return true;
    }

    boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color);
}
