package service;

import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private static final UserDAO USER_DAO = new SQLUserDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();

    public UserService() {}

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData user = USER_DAO.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Username and password do not match");
        }

        if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
            throw new UnauthorizedException("Username and password do not match");
        }

        String authToken = AUTH_DAO.createAuth(user);
        return new LoginResult(loginRequest.username(), authToken);
    }

    public LoginResult register(UserData user) throws DataAccessException {
        if (USER_DAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        USER_DAO.createUser(user);
        return new LoginResult(user.username(), AUTH_DAO.createAuth(user));
    }

    public void logout(String authToken) throws DataAccessException {
        AUTH_DAO.deleteAuth(authToken);
    }
}
