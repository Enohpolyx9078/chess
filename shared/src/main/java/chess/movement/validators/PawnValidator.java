package chess.movement.validators;

import chess.*;

public class PawnValidator implements MovementValidator{
    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        // Check out-of-bounds and self-capturing
        if (!MovementValidator.checkBasic(b, move, color)) {
            return false;
        }

        //TODO handle allowing the first move to be forward two
        // If the start row is 2 or 7 && both spaces are clear
        int startRow = move.getEndPosition().getRow();
        int targetRow = move.getStartPosition().getRow();
        if (Math.abs(startRow - targetRow) > 1) {
            ChessPosition intermediate = new ChessPosition(
                    (targetRow - startRow < 0) ? startRow - 1 : startRow + 1,
                    move.getStartPosition().getColumn());
            if (b.getPiece(intermediate) != null) {
                return false;
            }
            if (b.getPiece(move.getEndPosition()) != null) {
                return false;
            }
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
