package chess.rules;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EndGameEnforcer implements Enforcer {
    private static ChessPosition findKing(ChessGame.TeamColor color, ChessBoard board) {
        final int size = board.getSize();
        // for each space on the board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final ChessPosition checkPos = new ChessPosition(i, j);
                final ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece != null) {
                    if (
                            checkPiece.getTeamColor() == color
                                    && checkPiece.getPieceType() == ChessPiece.PieceType.KING
                    ) {
                        return checkPos;
                    }
                }
            }
        }
        // throw missing king error
        throw new RuntimeException("Could not find king");
    }

    private boolean kingIsTrapped(ChessGame.TeamColor color, ChessBoard board) {
        // check if every space the king can to move would put it in check
        final ChessPosition kingPos = findKing(color, board);
        final ChessPiece kingPiece = board.getPiece(kingPos);
        final Collection<ChessMove> moves = kingPiece.pieceMoves(board, kingPos);
        // for each space that's a move for the king
        for (ChessMove m : moves) {
            // create a hypothetical board where this move happened
            final ChessBoard hypothetical = new ChessBoard(board);
            hypothetical.addPiece(m.getStartPosition(), null);
            hypothetical.addPiece(m.getEndPosition(), kingPiece);
            if (!isInCheck(color, hypothetical)) {
                return false;
            }
        }
        return true;
    }

    private static List<ChessPosition> getOpposingTeam(ChessGame.TeamColor color, ChessBoard board) {
        final List<ChessPosition> opposingTeam = new ArrayList<>();
        final int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final ChessPosition checkPos = new ChessPosition(i, j);
                final ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece != null && checkPiece.getTeamColor() != color) {
                    opposingTeam.add(checkPos);
                }
            }
        }
        return opposingTeam;
    }

    @Override
    public boolean isInCheck(ChessGame.TeamColor color, ChessBoard board) {
        final ChessPosition kingPos = findKing(color, board);
        final List<ChessPosition> opposingTeam = getOpposingTeam(color, board);
        // for each piece on the opposing team
        for (ChessPosition pos : opposingTeam) {
            final ChessPiece p = board.getPiece(pos);
            final Collection<ChessMove> moves = p.pieceMoves(board, pos);
            if (moves.isEmpty()) {
                throw new RuntimeException("Could not find opposing team");
            }
            for (ChessMove m : moves) {
                // if a valid move lands on the king
                if (m.getEndPosition() == kingPos) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(ChessGame.TeamColor color, ChessBoard board) {
        // if every place the king could move to is in check && the king is in check
        return (kingIsTrapped(color, board) && isInCheck(color, board));
    }

    @Override
    public boolean isInStalemate(ChessGame.TeamColor color, ChessBoard board) {
        // if every place the king could move to is in check && the king is NOT in check
        return (kingIsTrapped(color, board) && !isInCheck(color, board));
    }
}
