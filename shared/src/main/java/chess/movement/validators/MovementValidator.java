package chess.movement.validators;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessBoard;

public interface MovementValidator {
    boolean isValid(ChessBoard b, ChessMove move, ChessGame.TeamColor color);
}
