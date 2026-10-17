package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

public class BoardPrinter {
    static private final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
    static private final int WIDTH = 3;
    static private final char[] COORDS = new char[] {'1', '2', '3', '4', '5', '6', '7', '8'};

    static private ChessGame.TeamColor swapColor(ChessGame.TeamColor currentColor) {
        return currentColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK :
                ChessGame.TeamColor.WHITE;
    }

    static private String setBeginColor(ChessGame.TeamColor currentColor, ChessPiece currentPiece) {
        final String BG_COLOR = currentColor == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_BG_COLOR_WHITE :
                EscapeSequences.SET_BG_COLOR_BLACK;
        if (currentPiece == null) {
            return BG_COLOR;
        }
        final String T_COLOR = currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_BLUE :
                EscapeSequences.SET_TEXT_COLOR_RED;
        return BG_COLOR + T_COLOR + EscapeSequences.SET_TEXT_BOLD;
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
        System.out.print("   ");
        for (ChessPiece chessPiece : row) {
            String beginColor = setBeginColor(currentColor, chessPiece);
            System.out.print(beginColor + " ".repeat(WIDTH) + RESET);
            currentColor = swapColor(currentColor);
        }
        System.out.print("   \n");
    }
    static private void printCharLine(ChessGame.TeamColor color, ChessPiece[] row, char label) {
        ChessGame.TeamColor currentColor = color;
        System.out.print(" " + label + " ");
        for (ChessPiece chessPiece : row) {
            String beginColor = setBeginColor(currentColor, chessPiece);
            System.out.print(beginColor + " ".repeat(WIDTH / 2) +
                    getIcon(chessPiece) +
                    " ".repeat(WIDTH / 2) + RESET);
            currentColor = swapColor(currentColor);
        }
        System.out.print(" " + label + " \n");
    }

    static private void printLetterLine(boolean reverse) {
        final char[] LETTERS = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        System.out.print("   ");
        for (var i = 0; i < LETTERS.length; i++) {
            System.out.print(" ".repeat(WIDTH / 2)
                    + (reverse ? LETTERS[LETTERS.length - (i + 1)] : LETTERS[i]) +
                    " ".repeat(WIDTH /2));
        }
        System.out.print("   \n");
    }

    static public void printRow(ChessPiece[] row, ChessGame.TeamColor startColor, char label) {
        // Set WIDTH to 9 and uncomment the color lines to print a big board
        //printColorLine(startColor, row);
        printCharLine(startColor, row, label);
        //printColorLine(startColor, row);
    }

    static public void printBoardBlack(ChessBoard board) {
        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(true);
        for (var i = 0; i < board.getSize(); i++) {
            printRow(board.getRow(i), currentColor, COORDS[i]);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(true);
    }

    static public void printBoardWhite(ChessBoard board) {
        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(false);
        for (var i = board.getSize() - 1; i >= 0; i--) {
            printRow(board.getRow(i), currentColor, COORDS[i]);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(false);
    }
}
