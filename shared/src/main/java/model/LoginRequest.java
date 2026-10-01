package model;

import java.util.Objects;

public record LoginRequest(String username, String password) {
    public LoginRequest {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }
}
