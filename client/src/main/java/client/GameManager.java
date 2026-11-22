package client;

import chess.ChessGame;

public class GameManager {
    private ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
