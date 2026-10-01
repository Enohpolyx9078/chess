package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    String createAuth(UserData user);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clear();
    int getSize();
}
