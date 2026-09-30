package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private static final Map<String, String> tokens = new HashMap<>();

    @Override
    public String createAuth(UserData user) {
        String token = UUID.randomUUID().toString();
        tokens.put(user.username().toLowerCase(), token);
        return token;
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
