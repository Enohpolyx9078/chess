package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;

public class GenericValidator implements MoveValidator {
    @Override
    public boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        return MoveValidator.isGenericallyValid(board, move, color);
    }
}
