package chess.movement.validators;

import chess.*;

import java.util.function.Function;

public class RookValidator implements MovementValidator{
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

        Function<int[], ChessPosition> getTarget;
        int shift;

        if (startRow == targetRow) {
            // moving horizontally
            shift = targetCol - startCol;
            if (shift < 0) {
                getTarget = (nums) -> new ChessPosition(nums[0], nums[1] - nums[2]);
            } else {
                getTarget = (nums) -> new ChessPosition(nums[0], nums[1] + nums[2]);
            }
        } else {
            // moving vertically
            shift = targetRow - startRow;
            if (shift < 0) {
                getTarget = (nums) -> new ChessPosition(nums[0] - nums[2], nums[1]);
            } else {
                getTarget = (nums) -> new ChessPosition(nums[0] + nums[2], nums[1]);
            }
        }

        for (int i = 1; i < Math.abs(shift); i++) {
            ChessPosition intermediate = getTarget.apply(new int[]{startRow + 1, startCol + 1, i});
            if (intermediate.getRow() < 0 || intermediate.getColumn() < 0
                || intermediate.getRow() > b.getSize() || intermediate.getColumn() > b.getSize()) {
                continue;
            }
            ChessPiece targetPiece = b.getPiece(intermediate);
            if (targetPiece != null) {
                return false;
            }
        }

        return true;
    }
}
