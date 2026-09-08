package chess.movement;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class KnightValidator implements MovementValidator {

    public KnightValidator () {

    }

    @Override
    public boolean isValid(ChessMove move, ChessPosition p, ChessGame.TeamColor color) {
        boolean valid = true;
        //TODO Check if the requested move is valid given p and color
        // A move is valid if:
        //  The endPosition is not on top of a piece of color
        //  The endPosition is on the board

        return valid;
    }
}
