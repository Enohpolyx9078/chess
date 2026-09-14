package chess.rules;

import chess.ChessBoard;
import chess.ChessGame;

public class EndGameEnforcer implements Enforcer {
    @Override
     public boolean isInCheck(ChessGame.TeamColor color, ChessBoard board) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isInCheckmate(ChessGame.TeamColor color, ChessBoard board) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isInStalemate(ChessGame.TeamColor color, ChessBoard board) {
        throw new RuntimeException("Not Implemented");
    }
}
