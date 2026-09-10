package chess.movement.validators;

import chess.*;

import java.util.function.Function;

public interface MovementValidator {
    // Performs the basic checks of out-of-bounds and overlapping your own team
    static boolean checkBasic(ChessBoard b, ChessMove move, ChessGame.TeamColor color) {
        final int[] posData = ChessBoard.interpretChessPosition(move.getEndPosition());
        final int targetRow = posData[0];
        final int targetCol = posData[1];

        // Check out of bounds
        if ((targetRow < 0 || targetCol < 0) || (targetRow >= b.getSize() || targetCol >= b.getSize())) {
            return false;
        }
        // Check capturing
        ChessPiece targetPiece = b.getPiece(move.getEndPosition());
        boolean targetOccupied = targetPiece != null;
        if (targetOccupied) {
            return targetPiece.getTeamColor() != color;
        }
        return true;
    }

    //Generates steps in a straight path
    static Function<int[], ChessPosition> describeStraight(int startRow, int startCol, int targetRow, int targetCol) {
        String traversal = (startRow == targetRow) ? "horizontal" : "vertical";
        int shift = (traversal.equals("horizontal")) ? (targetCol - startCol) : (targetRow - startRow);
        String offset = (shift < 0) ? "negative" : "positive";
        String direction = traversal + "-" + offset;

        return switch (direction) {
            case "horizontal-negative" -> (nums) -> new ChessPosition(nums[0], nums[1] - nums[2]);
            case "horizontal-positive" -> (nums) -> new ChessPosition(nums[0], nums[1] + nums[2]);
            case "vertical-negative" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1]);
            default -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1]);
        };
    }

    // Generates steps in a diagonal path
    static Function<int[], ChessPosition> describeDiagonal(int startRow, int startCol, int targetRow, int targetCol) {
        String horizontal = (startCol > targetCol) ? "left" : "right";
        String vertical = (startRow > targetRow) ? "down" : "up";
        String dir = horizontal + "-" + vertical;

        return switch (dir) {
            case "left-down" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1] - nums[2]);
            case "left-up" -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1] - nums[2]);
            case "right-down" -> (nums) -> new ChessPosition(nums[0] - nums[2], nums[1] + nums[2]);
            default -> (nums) -> new ChessPosition(nums[0] + nums[2], nums[1] + nums[2]);
        };
    }

    // Looks between the start and target for obstacles
    // The function passed in describes the path to traverse
    static boolean checkTraversal(ChessBoard b, int startRow, int startCol, int distance, Function<int[], ChessPosition> getTarget) {
        for (int i = 1; i < Math.abs(distance); i++) {
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

    boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color);
}
