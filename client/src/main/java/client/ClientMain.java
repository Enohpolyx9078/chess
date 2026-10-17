package client;

import chess.*;
import ui.BoardPrinter;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        BoardPrinter.printSquare(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        BoardPrinter.printSquare(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
    }
}
