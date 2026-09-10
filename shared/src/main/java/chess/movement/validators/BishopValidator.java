package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.function.Function;

public class BishopValidator implements MovementValidator{
    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        // Check out-of-bounds and self-capturing
        if (!MovementValidator.checkBasic(b, move, color)) {
            return false;
        }

        // Check if any piece exists between the target and the start
        final int[] startData = ChessBoard.interpretChessPosition(move.getStartPosition());
        final int startRow = startData[0];
        final int startCol = startData[1];
        final int[] targetData = ChessBoard.interpretChessPosition(move.getEndPosition());
        final int targetRow = targetData[0];
        final int targetCol = targetData[1];

        int shift = targetRow - startRow;
        return MovementValidator.checkTraversal(
                b, startRow, startCol, shift,
                MovementValidator.describeDiagonal(startRow, startCol, targetRow, targetCol)
        );
    }
}
