package chess.movement;

import chess.movement.validators.MovementValidator;

public class PawnStrategy extends MoveStrategy{
    public static final int[][] offsets = {
            {1, 0}, {1, -1}, {1, 1}
    };

    public PawnStrategy(MovementValidator v) {
        super(v, offsets);
    }
}
