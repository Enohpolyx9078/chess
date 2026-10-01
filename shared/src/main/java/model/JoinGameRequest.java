package model;

import java.util.Objects;

public record JoinGameRequest(Integer gameID, String playerColor, String authToken) {
    public JoinGameRequest {
        Objects.requireNonNull(gameID);
        Objects.requireNonNull(playerColor);
        Objects.requireNonNull(authToken);
    }
}
