package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;

public class PawnValidator implements MovementValidator{
    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        // Check out-of-bounds and self-capturing
        if (!MovementValidator.checkBasic(b, move, color)) {
            return false;
        }

        ChessPiece targetPiece = b.getPiece(move.getEndPosition());
        if (move.getStartPosition().getColumn() == move.getEndPosition().getColumn()) {
            // check that forward moves aren't blocked
            return targetPiece == null;
        } else {
            // check if diagonal moves are captures
            if (targetPiece != null) {
                return targetPiece.getTeamColor() != color;
            }
            return false;
        }
    }
}
