package chess.movement;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessBoard;
import chess.ChessPiece;

public class KnightValidator implements MovementValidator {

    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessPosition p, ChessGame.TeamColor color) {
        boolean valid = true;

        // Check if the requested move is valid given p and color
        final int[] posData = ChessBoard.interpretChessPosition(p);
        final int targetRow = posData[0];
        final int targetCol = posData[1];

        // Check out of bounds
        if ((targetRow < 0 || targetCol < 0) || (targetRow >= b.getSize() || targetCol >= b.getSize())) {
            return false;
        }
        // Check capturing
        ChessPiece targetPiece = b.getPiece(p);
        if (targetPiece == null || targetPiece.getTeamColor() == color) {
            return false;
        }

        return valid;
    }
}
