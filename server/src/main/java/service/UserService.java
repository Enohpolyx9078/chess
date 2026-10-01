package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.Objects;

public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public UserService() {}

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException{
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Username and password do not match");
        }

        // Eventually we'll replace this with checking password hashes
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException("Username and password do not match");
        }

        String authToken = authDAO.createAuth(user);
        return new LoginResult(loginRequest.username(), authToken);
    }

    public LoginResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        userDAO.createUser(user);
        return new LoginResult(user.username(), authDAO.createAuth(user));
    }

    public void logout(String authToken) throws UnauthorizedException{
        AuthData data = authDAO.getAuth(authToken);
        if (data == null) {
            throw new UnauthorizedException("Token is invalid");
        }
        authDAO.deleteAuth(authToken);
    }
}
