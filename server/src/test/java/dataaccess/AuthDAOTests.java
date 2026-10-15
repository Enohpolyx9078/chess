package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    static private final AuthDAO AUTH_DAO = new SQLAuthDAO();

    @Test
    public void clearAuthTest() throws DataAccessException {
        assertDoesNotThrow(AUTH_DAO::clear);
        assertEquals(0, AUTH_DAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        AUTH_DAO.clear();
        assertEquals(0, AUTH_DAO.getSize());
        AUTH_DAO.createAuth(new UserData("user", "pass", "email"));
        AUTH_DAO.createAuth(new UserData("user1", "pass1", "email1"));
        assertEquals(2, AUTH_DAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        AUTH_DAO.clear();
        UserData test = new UserData("mandycandy", "pass", "email");
        String token = AUTH_DAO.createAuth(test);
        assertEquals(new AuthData(token, test.username()), AUTH_DAO.getAuth(token));
    }

    @Test
    public void deleteTest() throws DataAccessException {
        AUTH_DAO.clear();
        String token = AUTH_DAO.createAuth(new UserData("oglee", "pass", "email"));
        AUTH_DAO.deleteAuth(token);
        assertEquals(0, AUTH_DAO.getSize());
    }
}
