package chess.movement;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessBoard;

public class KnightValidator implements MovementValidator {

    public KnightValidator () {

    }

    @Override
    public boolean isValid(ChessBoard b, ChessMove move, ChessPosition p, ChessGame.TeamColor color) {
        boolean valid = true;

        //TODO Check if the requested move is valid given p and color
        final int[] posData = ChessBoard.interpretChessPosition(p);
        final int targetRow = posData[0];
        final int targetCol = posData[1];

        // A move is valid if:
        //  The endPosition is on the board
        //  The endPosition is not on top of a piece of color

        return valid;
    }
}
