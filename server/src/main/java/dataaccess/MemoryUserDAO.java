package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{
    @Override
    public UserData getUser(String username) {
        //TODO
        return null;
    }

    @Override
    public boolean createUser(UserData user) {
        //TODO
        return false;
    }

    @Override
    public boolean createAuth(UserData user) {
        //TODO
        return false;
    }
}
