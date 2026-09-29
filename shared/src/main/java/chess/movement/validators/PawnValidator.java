package chess.movement.validators;

import chess.*;

public class PawnValidator implements MoveValidator{
    private boolean isFirstTurn(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        final int startRow = move.getStartPosition().getRow();
        return (color == ChessGame.TeamColor.WHITE) ?
                startRow == 2 :
                startRow == board.getSize() - 1;
    }

    private boolean isAttack(ChessMove move) {
        final ChessPosition start = move.getStartPosition();
        final ChessPosition end = move.getEndPosition();
        return (start.getRow() != end.getRow() &&
                start.getColumn() != end.getColumn());
    }

    @Override
    public boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        if (MoveValidator.isGenericallyValid(board, move, color)) {
            final ChessPiece target = board.getPiece(move.getEndPosition());
            if (Math.abs(MoveValidator.getDistance(move)) == 2 ) {
                return isFirstTurn(board, move, color) &&
                        MoveValidator.doesNotJump(board, move, MoveValidator.describeVertical(move)) &&
                        target == null;
            } else if (isAttack(move)) {
                return (target != null && target.getTeamColor() != color);
            }
            return target == null;
        }
        return false;
    }
}
