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
        return MovementValidator.checkBasic(b, move, color);
    }
}
