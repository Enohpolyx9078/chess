package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {
    static private final int width = 9;

    static private ChessGame.TeamColor swapColor(ChessGame.TeamColor currentColor) {
        return currentColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK :
                ChessGame.TeamColor.WHITE;
    }

    static private String setBeginColor(ChessGame.TeamColor currentColor) {
        return currentColor == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_BG_COLOR_WHITE :
                EscapeSequences.SET_BG_COLOR_BLACK;
    }

    static private char getIcon(ChessPiece piece) {
        char icon = ' ';
        if (piece != null) {
            icon = piece.getPieceType() == ChessPiece.PieceType.KNIGHT ?
                    'N' :
                    piece.getPieceType().name().charAt(0);
        }
        return icon;
    }

    static private void printColorLine(ChessGame.TeamColor color, ChessPiece[] row) {
        ChessGame.TeamColor currentColor = color;
        for (int i = 0; i < row.length; i++) {
            String beginColor = setBeginColor(currentColor);
            System.out.print(beginColor + " ".repeat(width) + EscapeSequences.RESET_BG_COLOR);
            currentColor = swapColor(currentColor);
        }
        System.out.print('\n');
    }
    static private void printCharLine(ChessGame.TeamColor color, ChessPiece[] row) {
        ChessGame.TeamColor currentColor = color;
        for (ChessPiece chessPiece : row) {
            String beginColor = setBeginColor(currentColor);
            System.out.print(beginColor + " ".repeat(width / 2) +
                    getIcon(chessPiece) +
                    " ".repeat(width / 2) + EscapeSequences.RESET_BG_COLOR);
            currentColor = swapColor(currentColor);
        }
        System.out.print('\n');
    }

    static public void printRow(ChessPiece[] row, ChessGame.TeamColor startColor) {
        printColorLine(startColor, row);
        printCharLine(startColor, row);
        printColorLine(startColor, row);
    }
}
