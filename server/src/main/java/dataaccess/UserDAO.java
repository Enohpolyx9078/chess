package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    boolean createUser(UserData user);
    boolean createAuth(UserData user);
}
