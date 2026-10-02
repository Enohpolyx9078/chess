package model;

import chess.ChessGame;

import java.util.Objects;

public record JoinGameRequest(Integer gameID, ChessGame.TeamColor playerColor) {
    public JoinGameRequest {
        Objects.requireNonNull(gameID);
        Objects.requireNonNull(playerColor);
    }
}
