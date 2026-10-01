package chess.movement;

import chess.movement.validators.MoveValidator;

public class RookStrategy extends MoveStrategy{
    private static final int[][] OFFSETS = {
            {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0},
            {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0},
            {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7},
            {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5}, {0, -6}, {0, -7}
    };

    public RookStrategy(MoveValidator v) {
        super(OFFSETS, v);
    }
}
