package chess.movement;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class KnightStrategy extends MoveStrategy {
    private final int[][] offsets = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-2, 1}, {-2, -2}, {-1, 2}, {-1, -2}
    };

    public KnightStrategy(MovementValidator v) {
        super(v);
    }

    private ChessPosition deriveTarget(ChessPosition p, int[] offset) {
        final int[] posData = ChessBoard.interpretChessPosition(p);
        final int curRow = posData[0];
        final int curCol = posData[1];
        return new ChessPosition(curRow + offset[0], curCol + offset[1]);
    }

    @Override
    Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b, ChessGame.TeamColor color) {
        return List.of();
    }
}
