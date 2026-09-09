package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;

public class PawnValidator implements MovementValidator{
    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        // Check out-of-bounds and self-capturing
        if (!MovementValidator.checkBasic(b, move, color)) {
            return false;
        }

        //TODO check if diagonal moves are captures

        return true;
    }
}
