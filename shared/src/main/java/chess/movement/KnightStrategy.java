package chess.movement;

import chess.movement.validators.MoveValidator;
public class KnightStrategy extends MoveStrategy {
    private static final int[][] OFFSETS = {
            {2, -1}, {2, 1}, {1, -2}, {1, 2},
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}
    };
    public KnightStrategy(MoveValidator v) {
        super(OFFSETS, v);
    }
}
