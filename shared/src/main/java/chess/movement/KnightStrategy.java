package chess.movement;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightStrategy extends MoveStrategy {
    private final int[][] offsets = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}
    };

    public KnightStrategy(MovementValidator v) {
        super(v);
    }

    private ChessMove deriveTarget(ChessPosition p, int[] offset) {
        final int[] posData = ChessBoard.interpretChessPosition(p);
        final int curRow = posData[0];
        final int curCol = posData[1];
        final ChessPosition end = new ChessPosition(curRow + offset[0] + 1, curCol + offset[1] + 1);
        return new ChessMove(p, end, null);
    }

    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b, ChessGame.TeamColor color) {
        // get a list of ChessMoves for any move that is valid
        // for each offset, derive the target move
        // if the target move is valid, add it to the final collection
        List<ChessMove> validMoves = new ArrayList<>();
        for (int[] offset : offsets) {
            ChessMove target = deriveTarget(p, offset);
            System.out.println("Target: " + target.toString());
            if (v.isValid(b, target, color)) {
                validMoves.add(target);
            }
        }
        return validMoves;
    }
}
