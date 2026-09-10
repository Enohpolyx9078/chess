package chess.movement.validators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;

public class QueenValidator implements MovementValidator{
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

        if (startRow != targetRow && startCol != targetCol) {
            int diagonalShift = targetRow - startRow;
            return MovementValidator.checkTraversal(b, startRow, startCol, diagonalShift,
                    MovementValidator.describeDiagonal(startRow, startCol, targetRow, targetCol)
            );
        } else {
            int straightShift = (startRow == targetRow) ? (targetCol - startCol) : (targetRow - startRow);
            return MovementValidator.checkTraversal(b, startRow, startCol, straightShift,
                    MovementValidator.describeStraight(startRow, startCol, targetRow, targetCol)
            );
        }
    }
}
