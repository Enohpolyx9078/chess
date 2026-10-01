package service;

import dataaccess.*;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.Objects;

public class UserService {
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();

    public UserService() {}

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException{
        UserData user = USER_DAO.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Username and password do not match");
        }

        // Eventually we'll replace this with checking password hashes
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException("Username and password do not match");
        }

        String authToken = AUTH_DAO.createAuth(user);
        return new LoginResult(loginRequest.username(), authToken);
    }

    public LoginResult register(UserData user) throws AlreadyTakenException {
        if (USER_DAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        USER_DAO.createUser(user);
        return new LoginResult(user.username(), AUTH_DAO.createAuth(user));
    }

    public void logout(String authToken) {
        AUTH_DAO.deleteAuth(authToken);
    }
}
