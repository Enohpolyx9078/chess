package chess.movement;

import chess.*;
import chess.movement.validators.GenericValidator;
import chess.movement.validators.MoveValidator;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MoveStrategy {
    private final int[][] OFFSETS;
    protected final MoveValidator v;

    protected MoveStrategy() {
        OFFSETS = new int[][]{};
        v = new GenericValidator();
    }

    protected MoveStrategy(int[][] offsets, MoveValidator v) {
        this.OFFSETS = offsets;
        this.v = v;
    }

    protected ChessMove deriveTarget(ChessPosition pos, int[] offset) {
        final ChessPosition target = new ChessPosition(
                pos.getRow() + offset[0], pos.getColumn() + offset[1]
        );
        return new ChessMove(pos, target, null);
    }

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        final Collection<ChessMove> valid = new ArrayList<>();
        for (int[] offset : OFFSETS) {
            final ChessMove move = deriveTarget(myPosition, offset);
            if (v.isValid(board, move, color)) {
                valid.add(move);
            }
        }
        return valid;
    }
}
