package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.sql.SQLAuthDAO;

public final class AuthUtility {
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();

    AuthUtility() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static boolean isAuthorized(String authToken) throws DataAccessException {
        return AUTH_DAO.getAuth(authToken) != null;
    }
}
