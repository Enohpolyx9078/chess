package model;

import chess.ChessGame;

import java.util.Objects;

public record JoinGameRequest(Integer gameID, ChessGame.TeamColor playerColor, String authToken) {
    public JoinGameRequest {
        Objects.requireNonNull(gameID);
        Objects.requireNonNull(playerColor);
        Objects.requireNonNull(authToken);
    }
}
