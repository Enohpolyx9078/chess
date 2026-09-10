package chess.movement;

import chess.movement.validators.MovementValidator;

public class BishopStrategy extends MoveStrategy{
    private final static int[][] offsets = {
            {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7},
            {-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7},
            {1, -1}, {2, -2}, {3, -3}, {4, -4}, {5, -5}, {6, -6}, {7, -7},
            {-1, -1}, {-2, -2}, {-3, -3}, {-4, -4}, {-5, -5}, {-6, -6}, {-7, -7}
    };
    public BishopStrategy(MovementValidator v) {
        super(v, offsets);
    }
}
