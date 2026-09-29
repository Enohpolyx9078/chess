package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.function.Function;

public class QueenValidator implements MoveValidator{
    @Override
    public boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        final MoveValidator.Direction dir = MoveValidator.getDirection(move);
        final Function<int[], ChessPosition> movement =
                switch(dir) {
                    case HORIZONTAL -> MoveValidator.describeHorizontal(move);
                    case VERTICAL -> MoveValidator.describeVertical(move);
                    case DIAGONAL -> MoveValidator.describeDiagonal(move);
                };

        return MoveValidator.isGenericallyValid(board, move, color) &&
                MoveValidator.doesNotJump(board, move, movement);
    }
}
