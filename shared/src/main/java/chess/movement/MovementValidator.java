package chess.movement;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessBoard;

public interface MovementValidator {
    boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color);
}
