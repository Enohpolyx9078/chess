package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    String createAuth(UserData user) throws DataAccessException;
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clear() throws DataAccessException;
    int getSize() throws DataAccessException;
}
