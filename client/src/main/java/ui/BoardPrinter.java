package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {
    static private final int width = 9;

    static public void printSquare(ChessGame.TeamColor color, ChessPiece piece) {
        final String BEGIN_COLOR = color == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_BG_COLOR_WHITE :
                EscapeSequences.SET_BG_COLOR_BLACK;
        final String BEGIN_TEXT = color == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_BLACK :
                EscapeSequences.SET_TEXT_COLOR_WHITE;
        final String SET_COLOR = BEGIN_COLOR + BEGIN_TEXT;
        char icon = ' ';
        if (piece != null) {
            icon = piece.getPieceType() == ChessPiece.PieceType.KNIGHT ?
                    'N' :
                    piece.getPieceType().name().charAt(0);
        }
        System.out.println(SET_COLOR + " ".repeat(width) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(SET_COLOR + " ".repeat(width / 2) +
                icon +
                " ".repeat(width/2) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(SET_COLOR + " ".repeat(width) + EscapeSequences.RESET_BG_COLOR);
    }

    static public void printCol(ChessBoard board, int col, ChessGame.TeamColor startColor) {
        ChessGame.TeamColor currentColor = startColor;
        for (var i = 1; i <= board.getSize(); i++) {
            ChessPiece chessPiece = board.getPiece(new ChessPosition(i, col));
            printSquare(currentColor, chessPiece);
            currentColor = currentColor == ChessGame.TeamColor.WHITE ?
                    ChessGame.TeamColor.BLACK :
                    ChessGame.TeamColor.WHITE;
        }
    }
}
