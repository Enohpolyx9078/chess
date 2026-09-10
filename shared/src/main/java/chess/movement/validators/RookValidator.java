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

        String traversal = (startRow == targetRow) ? "horizontal" : "vertical";
        int shift = (traversal.equals("horizontal")) ? (targetCol - startCol) : (targetRow - startRow);
        String offset = (shift < 0) ? "negative" : "positive";
        String direction = traversal + "-" + offset;

        getTarget = switch (direction) {
            case "horizontal-negative" -> (nums) -> new ChessPosition(nums[0], nums[1] - nums[2]);
            case "horizontal-positive" -> (nums) -> new ChessPosition(nums[0], nums[1] + nums[2]);
            case "vertical-negative" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1]);
            default -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1]);
        };

        return MovementValidator.checkTraversal(b, startRow, startCol, shift, getTarget);
    }
}
