package chess.movement.validators;

import chess.*;

import java.util.function.Function;

public class RookValidator implements MoveValidator{
    @Override
    public boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        final MoveValidator.Direction dir = MoveValidator.getDirection(move);
        final Function<int[], ChessPosition> movement =
                (dir == Direction.HORIZONTAL) ?
                        MoveValidator.describeHorizontal(move) :
                        MoveValidator.describeVertical(move);

        return MoveValidator.isGenericallyValid(board, move, color) &&
                MoveValidator.doesNotJump(board, move, movement);
    }
}
