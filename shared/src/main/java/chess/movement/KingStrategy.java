package chess.movement;

public class KingStrategy extends MoveStrategy{
    private static final int[][] offsets = {
            {1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}
    };
    public KingStrategy(MovementValidator v) {
        super(v, offsets);
    }
}
