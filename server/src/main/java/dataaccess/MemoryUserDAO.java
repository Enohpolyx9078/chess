package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private static final Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return users.get(username.toLowerCase());
    }

    @Override
    public void createUser(UserData user) {
        UserData record = new UserData(
                user.username().toLowerCase(),
                user.password(),
                user.email().toLowerCase());
        users.put(record.username(), record);
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public int getSize() {
        return users.size();
    }
}
