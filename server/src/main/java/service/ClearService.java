package service;

import dataaccess.*;

public class ClearService {
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    private static final GameDAO gameDAO = new MemoryGameDAO();

    public ClearService() {}

    public void clearApplication() {
        clearUsers();
        clearAuths();
        clearGames();
    }

    private void clearUsers() {
        userDAO.clear();
    }

    private void clearAuths() {
        authDAO.clear();
    }

    private void clearGames() {
        gameDAO.clear();
    }
}
