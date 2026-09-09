package chess.movement;

import chess.ChessGame;
import chess.movement.validators.MovementValidator;

public class PawnStrategy extends MoveStrategy{
    public static final int[][] offsetsWhite = {
            {1, 0}, {1, -1}, {1, 1},
    };
    public static final int[][] offsetsBlack = {
            {-1, 0}, {-1, -1}, {-1, 1},
    };

    public PawnStrategy(MovementValidator v, ChessGame.TeamColor color) {
        super(v, (color == ChessGame.TeamColor.BLACK) ? offsetsBlack : offsetsWhite);
    }
}
