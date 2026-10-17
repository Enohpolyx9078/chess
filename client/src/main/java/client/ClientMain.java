package client;

import chess.*;
import ui.BoardPrinter;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        ChessPiece[] row = new ChessPiece[] {
                new ChessPiece(color, ChessPiece.PieceType.ROOK),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.QUEEN),
                new ChessPiece(color, ChessPiece.PieceType.KING),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.ROOK),
        };
        BoardPrinter.printRow(row, color);
    }
}
