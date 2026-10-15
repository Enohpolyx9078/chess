package dataaccess.sql;

import dataaccess.DataAccessException;
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
    public void clear() throws DataAccessException {
        SQLUtility.clearTable("user");
    }

    @Override
    public int getSize() throws DataAccessException {
        return SQLUtility.getTableSize("user");
    }
}
