package chess.movement;

import chess.movement.validators.MoveValidator;

public class KingStrategy extends MoveStrategy{
    private static final int[][] OFFSETS = {
            {1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}
    };
    public KingStrategy(MoveValidator v) {
        super(OFFSETS, v);
    }
}
