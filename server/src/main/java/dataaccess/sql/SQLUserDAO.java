package dataaccess.sql;

import dataaccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
