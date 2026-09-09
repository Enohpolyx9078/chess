package chess.movement;

import chess.ChessPosition;
import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessGame;
import chess.movement.validators.MovementValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MoveStrategy {
    // Child classes should have a Collection<List<Integer>> offsets parameter
    // The child objects in the List should look like {int rowMove, int colMove}
    protected MovementValidator v;
    protected int[][] offsets;

    public MoveStrategy(MovementValidator v, int[][] offsets) {
        this.v = v;
        this.offsets = offsets;
    }

    private ChessMove deriveTarget(ChessPosition p, int[] offset) {
        final int[] posData = ChessBoard.interpretChessPosition(p);
        final int curRow = posData[0];
        final int curCol = posData[1];
        final ChessPosition end = new ChessPosition(curRow + offset[0] + 1, curCol + offset[1] + 1);
        return new ChessMove(p, end, null);
    }

    public Collection<ChessMove> getValidMoves(ChessPosition p, ChessBoard b, ChessGame.TeamColor color) {
        // get a list of ChessMoves for any move that is valid
        // for each offset, derive the target move
        // if the target move is valid, add it to the final collection
        List<ChessMove> validMoves = new ArrayList<>();
        for (int[] offset : offsets) {
            ChessMove target = deriveTarget(p, offset);
            if (v.isValid(b, target, color)) {
                validMoves.add(target);
            }
        }
        return validMoves;
    }
}
