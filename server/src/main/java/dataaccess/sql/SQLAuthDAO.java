package dataaccess.sql;

import dataaccess.AuthDAO;
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
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
