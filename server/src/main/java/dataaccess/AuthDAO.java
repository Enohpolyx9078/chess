package dataaccess;

import model.UserData;

public interface AuthDAO {
    String createAuth(UserData user);
}
