package client;

import chess.ChessGame;

public class GameManager {
    private ChessGame game;
    private ChessGame.TeamColor teamColor;

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setTeamColor(ChessGame.TeamColor color) {
        this.teamColor = color;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
