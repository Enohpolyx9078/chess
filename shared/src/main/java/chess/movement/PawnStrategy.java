package chess.movement;

import chess.*;
import chess.movement.validators.MoveValidator;

import java.util.ArrayList;
import java.util.Collection;

public class PawnStrategy extends MoveStrategy {
    private static final int[][] WHITE_OFFSETS = {
            {1, 0}, {1, -1}, {1, 1}, {2, 0}
    };
    private static final int[][] BLACK_OFFSETS = {
            {-1, 0}, {-1, -1}, {-1, 1}, {-2, 0}
    };

    private final int[][] offsets;
    private final MoveValidator v;

    public PawnStrategy(ChessGame.TeamColor color, MoveValidator v) {
        this.offsets = (color == ChessGame.TeamColor.WHITE) ?
                WHITE_OFFSETS : BLACK_OFFSETS;
        this.v = v;
    }

    private boolean isPromotion(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        final int endRow = move.getEndPosition().getRow();
        return (color == ChessGame.TeamColor.WHITE) ?
                endRow == board.getSize() :
                endRow == 1;
    }

    private Collection<ChessMove> getPromotions(ChessMove move) {
        final Collection<ChessMove> promos = new ArrayList<>();
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.PAWN) {
                continue;
            }
            promos.add(new ChessMove(move, type));
        }
        return promos;
    }

    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        final Collection<ChessMove> valid = new ArrayList<>();
        for (int[] offset : offsets) {
            final ChessMove move = deriveTarget(myPosition, offset);
            if (v.isValid(board, move, color)) {
                if (isPromotion(board, move, color)) {
                    valid.addAll(getPromotions(move));
                } else {
                    valid.add(move);
                }
            }
        }
        return valid;
    }
}
