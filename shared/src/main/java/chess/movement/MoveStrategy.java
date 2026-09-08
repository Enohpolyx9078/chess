package chess.movement;

import chess.ChessPosition;
import chess.ChessMove;
import chess.ChessBoard;

import java.util.Collection;

public abstract class MoveStrategy {
    protected MovementValidator v;

    public MoveStrategy(MovementValidator v) {
        this.v = v;
    }

    abstract Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b);
}
