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

        Function<int[], ChessPosition> getTarget;
        int shift = targetRow - startRow;

        String horizontal = (startCol > targetCol) ? "left" : "right";
        String vertical = (startRow > targetRow) ? "down" : "up";
        String dir = horizontal + "-" + vertical;

        getTarget = switch (dir) {
            case "left-down" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1] - nums[2]);
            case "left-up" -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1] - nums[2]);
            case "right-down" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1] + nums[2]);
            default -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1] + nums[2]);
        };

        return MovementValidator.checkTraversal(b, startRow, startCol, shift, getTarget);
    }
}
