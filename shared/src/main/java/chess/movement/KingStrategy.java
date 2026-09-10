package chess.movement;

import chess.movement.validators.MovementValidator;

public class KingStrategy extends MoveStrategy{
    private static final int[][] OFFSETS = {
            {1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}
    };
    public KingStrategy(MovementValidator v) {
        super(v, OFFSETS);
    }
}
