package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    String createAuth(UserData user) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    int getSize() throws DataAccessException;
}
