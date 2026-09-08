package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightStrategy extends MoveStrategy {
    private final int[][] offsets = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-2, 1}, {-2, -2}, {-1, 2}, {-1, -2}
    };

    public KnightStrategy(MovementValidator v) {
        super(v);
    }

    @Override
    Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b) {
        return List.of();
    }
}
