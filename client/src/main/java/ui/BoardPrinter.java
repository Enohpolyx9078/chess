package ui;

import chess.ChessGame;
import chess.ChessPiece;

public class BoardPrinter {
    static private final int width = 9;

    static public void printSquare(ChessGame.TeamColor color, ChessPiece.PieceType piece) {
        final String beginColor = color == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_BG_COLOR_WHITE :
                EscapeSequences.SET_BG_COLOR_BLACK;
        final String beginText = color == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_BLACK :
                EscapeSequences.SET_TEXT_COLOR_WHITE;
        final String setColor = beginColor + beginText;
        final char icon = piece == ChessPiece.PieceType.KNIGHT ?
                'N' :
                piece.name().charAt(0);
        System.out.println(setColor + " ".repeat(width) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(setColor + " ".repeat(width / 2) +
                icon +
                " ".repeat(width/2) + EscapeSequences.RESET_BG_COLOR);
        System.out.println(setColor + " ".repeat(width) + EscapeSequences.RESET_BG_COLOR);
    }
}
