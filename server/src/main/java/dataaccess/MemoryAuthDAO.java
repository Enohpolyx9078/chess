package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private static final Map<String, String> TOKENS = new HashMap<>();

    @Override
    public String createAuth(UserData user) {
        String token = UUID.randomUUID().toString();
        TOKENS.put(token, user.username().toLowerCase());
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        String username = TOKENS.get(authToken);
        if (username == null) {
            return null;
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) {
        TOKENS.remove(authToken);
    }

    @Override
    public void clear() {
        TOKENS.clear();
    }

    @Override
    public int getSize() {
        return TOKENS.size();
    }
}
