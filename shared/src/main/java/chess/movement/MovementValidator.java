package chess.movement;

import chess.*;

public interface MovementValidator {
    boolean isValid(ChessMove move, ChessPosition p, ChessGame.TeamColor color);
}
