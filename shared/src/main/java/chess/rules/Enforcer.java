package chess.rules;

import chess.ChessBoard;
import chess.ChessGame;

public interface Enforcer {
    boolean isInCheck(ChessGame.TeamColor color, ChessBoard board);
    boolean isInCheckmate(ChessGame.TeamColor color, ChessBoard board);
    boolean isInStalemate(ChessGame.TeamColor color, ChessBoard board);
}
