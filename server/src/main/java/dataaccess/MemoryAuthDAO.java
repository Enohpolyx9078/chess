package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private static final Map<String, String> tokens = new HashMap<>();

    @Override
    public String createAuth(UserData user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.username().toLowerCase());
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        String username = tokens.get(authToken);
        if (username == null) {
            return null;
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) {
        tokens.remove(authToken);
    }

    @Override
    public void clear() {
        tokens.clear();
    }

    @Override
    public int getSize() {
        return tokens.size();
    }
}
