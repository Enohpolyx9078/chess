package chess.movement;

import chess.movement.validators.MovementValidator;

public class KnightStrategy extends MoveStrategy {
    private static final int[][] OFFSETS = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}
    };

    public KnightStrategy(MovementValidator v) {
        super(v, OFFSETS);
    }
}
