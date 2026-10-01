package chess.movement;

import chess.movement.validators.MoveValidator;

public class BishopStrategy extends MoveStrategy{
    private static final int[][] OFFSETS = {
            {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7},
            {1, -1}, {2, -2}, {3, -3}, {4, -4}, {5, -5}, {6, -6}, {7, -7},
            {-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7},
            {-1, -1}, {-2, -2}, {-3, -3}, {-4, -4}, {-5, -5}, {-6, -6}, {-7, -7}
    };
    public BishopStrategy(MoveValidator v) {
        super(OFFSETS, v);
    }
}
