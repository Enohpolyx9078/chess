package client;

import chess.ChessGame;

public class GameManager {
    private ChessGame game;
    private ChessGame.TeamColor teamColor;
    private String latestMessage;
    private int gameCode;

    public void reset() {
        game = null;
        teamColor = null;
        latestMessage = null;
    }

    public int getGameCode() {
        return gameCode;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setTeamColor(ChessGame.TeamColor color) {
        this.teamColor = color;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
