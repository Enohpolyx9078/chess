package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private static final Map<String, UserData> USERS = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return USERS.get(username);
    }

    @Override
    public void createUser(UserData user) {
        UserData record = new UserData(
                user.username(),
                user.password(),
                user.email().toLowerCase());
        USERS.put(record.username(), record);
    }

    @Override
    public void clear() {
        USERS.clear();
    }

    @Override
    public int getSize() {
        return USERS.size();
    }
}
