package chess.movement.validators;

import chess.*;

import java.util.function.Function;

public interface MoveValidator {

    private static boolean isInBounds(ChessBoard board, ChessMove move) {
        final int size = board.getSize();
        final ChessPosition target = move.getEndPosition();
        final int targetRow = target.getRow();
        final int targetCol = target.getColumn();
        return targetRow > 0 && targetRow <= size &&
                targetCol > 0 && targetCol <= size;
    }

    private static boolean isNotSelfCapture(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        final ChessPiece target = board.getPiece(move.getEndPosition());
        if (target != null) {
            return target.getTeamColor() != color;
        }
        return true;
    }

    static Direction getDirection(ChessMove move) {
        final ChessPosition start = move.getStartPosition();
        final ChessPosition end = move.getEndPosition();

        if (start.getRow() != end.getRow() && start.getColumn() != end.getColumn()) {
            return Direction.DIAGONAL;
        }
        return (start.getRow() != end.getRow()) ? Direction.VERTICAL : Direction.HORIZONTAL;
    }

    static int getDistance(ChessMove move) {
        final ChessPosition start = move.getStartPosition();
        final ChessPosition end = move.getEndPosition();
        return switch (getDirection(move)) {
            case HORIZONTAL, DIAGONAL -> end.getColumn() - start.getColumn();
            case VERTICAL -> end.getRow() - start.getRow();
        };
    }

    static Function<int[], ChessPosition> describeHorizontal(ChessMove move) {
        return (move.getStartPosition().getColumn() > move.getEndPosition().getColumn()) ?
                (int[] args) -> new ChessPosition(args[0], args[1] - args[2]) :
                (int[] args) -> new ChessPosition(args[0], args[1] + args[2]);
    }
    static Function<int[], ChessPosition> describeVertical(ChessMove move) {
        return (move.getStartPosition().getRow() > move.getEndPosition().getRow()) ?
                (int[] args) -> new ChessPosition(args[0] - args[2], args[1]) :
                (int[] args) -> new ChessPosition(args[0] + args[2], args[1]);
    }

    static Function<int[], ChessPosition> describeDiagonal(ChessMove move) {
        final ChessPosition start = move.getStartPosition();
        final ChessPosition end = move.getEndPosition();
        if (start.getRow() > end.getRow()) {
            return (move.getStartPosition().getColumn() > move.getEndPosition().getColumn()) ?
                    (int[] args) -> new ChessPosition(args[0] - args[2], args[1] - args[2]) :
                    (int[] args) -> new ChessPosition(args[0] - args[2], args[1] + args[2]);
        } else {
            return (move.getStartPosition().getColumn() > move.getEndPosition().getColumn()) ?
                    (int[] args) -> new ChessPosition(args[0] + args[2], args[1] - args[2]) :
                    (int[] args) -> new ChessPosition(args[0] + args[2], args[1] + args[2]);
        }
    }

    static boolean doesNotJump(ChessBoard board, ChessMove move, Function<int[], ChessPosition> getIntermediate) {
        final ChessPosition start = move.getStartPosition();
        for (int i = 1; i < Math.abs(getDistance(move)); i++) {
            final ChessPosition intermediate = getIntermediate.apply(
                    new int[] {start.getRow(), start.getColumn(), i}
            );
            if (isInBounds(board, new ChessMove(start, intermediate, null)) &&
                    board.getPiece(intermediate) != null) {
                return false;
            }
        }
        return true;
    }

    static boolean isGenericallyValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        return isInBounds(board, move) && isNotSelfCapture(board, move, color);
    }

    enum Direction {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL
    }

    boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color);
}
