package chess.rules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class EndGameEnforcer implements Enforcer {
    private static ChessPosition findKing(ChessGame.TeamColor color, ChessBoard board) {
        // find the king's position and return it
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

    private static boolean kingIsTrapped(ChessGame.TeamColor color, ChessBoard board) {
        //TODO check if every space the king can to move would put it in check
        // find the position of color's king
        // for each space that's a move for the king
        //    create a hypothetical board where this move happened
        //    if !isInCheck(color, hypothetical)
        //        return false
        // return true
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isInCheck(ChessGame.TeamColor color, ChessBoard board) {
        //TODO check if color is in check
        // find the position of color's king
        // for each piece on the opposing team
        //    if a valid move lands on the king
        //        return true
        // return false
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isInCheckmate(ChessGame.TeamColor color, ChessBoard board) {
        //TODO find the position of color's king
        // if every place the king could move to is in check && the king is in check
        //    return true
        // return false
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isInStalemate(ChessGame.TeamColor color, ChessBoard board) {
        //TODO find the position of color's king
        // if every place the king could move to is in check && the king is NOT in check
        //    return true
        // return false
        throw new RuntimeException("Not Implemented");
    }
}
