package model;

import java.util.Objects;

public record GameRequest(String gameName) {
    public GameRequest {
        Objects.requireNonNull(gameName);
    }
}
