package service;

import dataaccess.*;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public UserService() {}

    public static LoginResult login(LoginRequest loginRequest) throws UnauthorizedException{
        UserData user = userDAO.getUser(loginRequest.username());
        //TODO check the password (eventually the password hash)
        if (user == null) {
            throw new UnauthorizedException("Username and password do not match");
        }
        authDAO.createAuth(user);
        //TODO return a loginResult
        return null;
    }

    public LoginResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        userDAO.createUser(user);
        return new LoginResult(user.username(), authDAO.createAuth(user));
    }
}
