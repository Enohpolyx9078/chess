package chess.movement;

import chess.ChessPosition;
import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessGame;

import java.util.Collection;

public abstract class MoveStrategy {
    // Child classes should have a Collection<List<Integer>> offsets parameter
    // The child objects in the List should look like {int rowMove, int colMove}
    protected MovementValidator v;

    public MoveStrategy(MovementValidator v) {
        this.v = v;
    }

    public abstract Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b, ChessGame.TeamColor color);
}
