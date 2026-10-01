package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;

public class AuthService {
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public AuthService() {}

    public static boolean isAuthorized(String authToken) {
        return authDAO.getAuth(authToken) != null;
    }
}
