package service;

import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;

public class ClearService {
    private static final UserDAO USER_DAO = new SQLUserDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();
    private static final GameDAO GAME_DAO = new SQLGameDAO();

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
