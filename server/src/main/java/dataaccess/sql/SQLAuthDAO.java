package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public String createAuth(UserData user) {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() throws DataAccessException {
        SQLUtility.clearTable("auth");
    }

    @Override
    public int getSize() throws DataAccessException {
        return SQLUtility.getTableSize("auth");
    }
}
