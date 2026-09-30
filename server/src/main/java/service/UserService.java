package service;

import dataaccess.*;
import model.RegisterResult;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        userDAO.createUser(user);
        return new RegisterResult(user.username().toLowerCase(), authDAO.createAuth(user));
    }
}
