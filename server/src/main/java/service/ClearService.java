package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;

public class ClearService {
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public ClearService() {}

    public void clearApplication() {
        clearUsers();
        clearAuths();
        clearGames();
    }

    private void clearUsers() {
        USER_DAO.clear();
    }

    private void clearAuths() {
        AUTH_DAO.clear();
    }

    private void clearGames() {
        GAME_DAO.clear();
    }
}
