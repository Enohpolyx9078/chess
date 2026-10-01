package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;

public final class AuthUtility {
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    AuthUtility() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static boolean isAuthorized(String authToken) {
        return authDAO.getAuth(authToken) != null;
    }
}
