package chess.movement.validators;

import chess.*;

import java.util.function.Function;

public class BishopValidator implements MoveValidator{
    @Override
    public boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        return MoveValidator.isGenericallyValid(board, move, color) &&
                MoveValidator.doesNotJump(board, move, MoveValidator.describeDiagonal(move));
    }
}