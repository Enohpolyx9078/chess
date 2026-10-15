package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    static private final AuthDAO authDAO = new SQLAuthDAO();

    @Test
    public void clearAuthTest() throws DataAccessException {
        assertDoesNotThrow(authDAO::clear);
        assertEquals(0, authDAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        authDAO.clear();
        assertEquals(0, authDAO.getSize());
        authDAO.createAuth(new UserData("user", "pass", "email"));
        authDAO.createAuth(new UserData("user1", "pass1", "email1"));
        assertEquals(2, authDAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        authDAO.clear();
        UserData test = new UserData("mandycandy", "pass", "email");
        String token = authDAO.createAuth(test);
        assertEquals(new AuthData(token, test.username()), authDAO.getAuth(token));
    }

    @Test
    public void deleteTest() throws DataAccessException {
        authDAO.clear();
        String token = authDAO.createAuth(new UserData("oglee", "pass", "email"));
        authDAO.deleteAuth(token);
        assertEquals(0, authDAO.getSize());
    }
}
