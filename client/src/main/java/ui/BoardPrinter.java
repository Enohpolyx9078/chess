package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BoardPrinter {
    static private final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
    static private final int WIDTH = 3;
    static private final char[] COORDS = new char[] {'1', '2', '3', '4', '5', '6', '7', '8'};

    static private ChessGame.TeamColor swapColor(ChessGame.TeamColor currentColor) {
        return currentColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK :
                ChessGame.TeamColor.WHITE;
    }

    static private String setBeginColor(ChessGame.TeamColor currentColor, ChessPiece currentPiece,
                                        boolean matchesTarget) {
        final String bgColor;
        if (!matchesTarget) {
            bgColor = currentColor == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.SET_BG_COLOR_WHITE :
                    EscapeSequences.SET_BG_COLOR_BLACK;
        } else {
            bgColor = currentColor == ChessGame.TeamColor.WHITE ?
                    EscapeSequences.SET_BG_COLOR_GREEN :
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        }
        if (currentPiece == null) {
            return bgColor;
        }
        final String tColor = currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_BLUE :
                EscapeSequences.SET_TEXT_COLOR_RED;
        return bgColor + tColor + EscapeSequences.SET_TEXT_BOLD;
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

    private static boolean matchesTarget(int row, int col, Collection<ChessPosition> targets) {
        for (ChessPosition p : targets) {
            if ((row + 1) == p.getRow() &&
                    (col + 1) == p.getColumn()) {
                return true;
            }
        }
        return false;
    }

    private static String setBgYellow(ChessPiece currentPiece) {
        final String tColor = currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                EscapeSequences.SET_TEXT_COLOR_BLUE :
                EscapeSequences.SET_TEXT_COLOR_RED;
        return EscapeSequences.SET_BG_COLOR_YELLOW + tColor + EscapeSequences.SET_TEXT_BOLD;
    }

    private static boolean isStart(ChessPosition start, int rowIndex, int col) {
        if (start == null) {
            return false;
        }
        return (start.getRow() - 1) == rowIndex &&
                (start.getColumn() - 1) == col;
    }

    private static void printCharLine(ChessGame.TeamColor color, ChessPiece[] row, char label,
                                      boolean reverse, Collection<ChessPosition> targets,
                                      int rowIndex, ChessPosition start) {
        ChessGame.TeamColor currentColor = color;
        System.out.print(" " + label + " ");
        for (int i = 0; i < row.length; i++) {
            ChessPiece chessPiece = reverse ? row[row.length - (i + 1)] : row[i];
            int adjustedRowIndex = reverse ? rowIndex : row.length - (rowIndex + 1);
            int adjustedColIndex = reverse ? i : row.length - (rowIndex + 1);
            String beginColor = isStart(start, adjustedRowIndex, adjustedColIndex) ?
                setBgYellow(chessPiece) :
                setBeginColor(
                        currentColor,
                        chessPiece,
                        matchesTarget(adjustedRowIndex,
                                i,
                                targets));
            System.out.print(beginColor + " ".repeat(WIDTH / 2) +
                    getIcon(chessPiece) +
                    " ".repeat(WIDTH / 2) + RESET);
            currentColor = swapColor(currentColor);
        }
        System.out.print(" " + label + " \n");
    }

    static private void printLetterLine(boolean reverse) {
        final char[] letters = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        System.out.print("   ");
        for (var i = 0; i < letters.length; i++) {
            System.out.print(" ".repeat(WIDTH / 2)
                    + (reverse ? letters[letters.length - (i + 1)] : letters[i]) +
                    " ".repeat(WIDTH /2));
        }
        System.out.print("   \n");
    }

    static public void printRow(ChessPiece[] row, ChessGame.TeamColor startColor,
                                char label, boolean reverse, Collection<ChessPosition> targets,
                                int rowIndex, ChessPosition start) {
        printCharLine(startColor, row, label, reverse, targets, rowIndex, start);
    }

    static public void printHighlightBlack(ChessPosition start, ChessBoard board) {
        ChessPiece piece = board.getPiece(start);
        Collection<ChessMove> moves = piece.pieceMoves(board, start);
        Collection<ChessPosition> targets = new ArrayList<>();
        moves.forEach((m) -> targets.add(m.getEndPosition()));

        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(true);
        for (var i = 0; i < board.getSize(); i++) {
            printRow(board.getRow(i), currentColor, COORDS[i], true, targets, i, start);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(true);
    }

    static public void printHighlightWhite(ChessPosition start, ChessBoard board) {
        ChessPiece piece = board.getPiece(start);
        Collection<ChessMove> moves = piece.pieceMoves(board, start);
        Collection<ChessPosition> targets = new ArrayList<>();
        moves.forEach((m) -> targets.add(m.getEndPosition()));

        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(false);
        for (var i = board.getSize() - 1; i >= 0; i--) {
            printRow(board.getRow(i), currentColor, COORDS[i], false, targets, i, start);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(false);
    }

    static public void printBoardBlack(ChessBoard board) {
        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(true);
        for (var i = 0; i < board.getSize(); i++) {
            printRow(board.getRow(i), currentColor, COORDS[i], true, new ArrayList<>(), i, null);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(true);
    }

    static public void printBoardWhite(ChessBoard board) {
        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        printLetterLine(false);
        for (var i = board.getSize() - 1; i >= 0; i--) {
            printRow(board.getRow(i), currentColor, COORDS[i], false, new ArrayList<>(), i, null);
            currentColor = swapColor(currentColor);
        }
        printLetterLine(false);
    }
}
