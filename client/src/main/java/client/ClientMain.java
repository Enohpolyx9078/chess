package client;

import chess.*;
import ui.BoardPrinter;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        BoardPrinter.printBoardWhite(board);
        System.out.println("********************************");
        BoardPrinter.printBoardBlack(board);
    }
}
