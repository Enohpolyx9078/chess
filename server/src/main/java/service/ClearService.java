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

    public void clearApplication() throws DataAccessException {
        clearUsers();
        clearAuths();
        clearGames();
    }

    private void clearUsers() throws DataAccessException { USER_DAO.clear(); }

    private void clearAuths() throws DataAccessException {
        AUTH_DAO.clear();
    }

    private void clearGames() throws DataAccessException {
        GAME_DAO.clear();
    }
}
