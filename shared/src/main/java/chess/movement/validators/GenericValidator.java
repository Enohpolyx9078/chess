package chess.movement.validators;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessPiece;

// This validator only check if the move is out-of-bounds
// or if it results in a valid capture
public class GenericValidator implements MovementValidator {

    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        // Check if the requested move is valid given move and color
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
}
