package dataaccess;

import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static private final UserDAO userDAO = new SQLUserDAO();

    @Test
    public void clearUsersTest() {
        assertDoesNotThrow(userDAO::clear);
        assertEquals(0, userDAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        userDAO.clear();
        assertEquals(0, userDAO.getSize());
        userDAO.createUser(new UserData("user", "pass", "email"));
        userDAO.createUser(new UserData("user1", "pass1", "email1"));
        assertEquals(2, userDAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        userDAO.clear();
        UserData test = new UserData("enohpolyx", "pass", "email");
        userDAO.createUser(test);
        assertEquals(test, userDAO.getUser(test.username()));
    }
}
