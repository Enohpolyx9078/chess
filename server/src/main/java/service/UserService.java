package service;

import dataaccess.*;
import model.RegisterResult;
import model.UserData;

public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public UserService() {}

    public RegisterResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        userDAO.createUser(user);
        return new RegisterResult(user.username(), authDAO.createAuth(user));
    }
}
