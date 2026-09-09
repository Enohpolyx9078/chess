package chess.movement;

import chess.*;
import chess.movement.validators.MovementValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private List<ChessMove> addPromotions(ChessMove move) {
        List<ChessMove> moves = new ArrayList<>();
        for (ChessPiece.PieceType t : ChessPiece.PieceType.values()) {
            if (t == ChessPiece.PieceType.PAWN) {
                continue;
            }
            moves.add(new ChessMove(move, t));
        }
        return moves;
    }

    @Override
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
        if (!validMoves.isEmpty()) {
            int targetRow = validMoves.getFirst().getEndPosition().getRow();
            if (targetRow == 1 || targetRow == b.getSize()) {
                List<ChessMove> promotions = new ArrayList<>();
                for (ChessMove move : validMoves) {
                    promotions.addAll(addPromotions(move));
                }
                return promotions;
            }
        }
        return validMoves;
    }
}
