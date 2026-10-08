package service;

import dataaccess.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;

public final class AuthUtility {
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();

    AuthUtility() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static boolean isAuthorized(String authToken) {
        return AUTH_DAO.getAuth(authToken) != null;
    }
}
