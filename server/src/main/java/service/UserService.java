package service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.RegisterResult;
import model.UserData;

public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();
    public RegisterResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        userDAO.createUser(user);
        return new RegisterResult(user.username().toLowerCase(), userDAO.createAuth(user));
    }
}
