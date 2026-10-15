package dataaccess;

import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static private final UserDAO USER_DAO = new SQLUserDAO();

    @Test
    public void clearUsersTest() throws DataAccessException {
        assertDoesNotThrow(USER_DAO::clear);
        assertEquals(0, USER_DAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        USER_DAO.clear();
        assertEquals(0, USER_DAO.getSize());
        USER_DAO.createUser(new UserData("user", "pass", "email"));
        USER_DAO.createUser(new UserData("user1", "pass1", "email1"));
        assertEquals(2, USER_DAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        USER_DAO.clear();
        UserData test = new UserData("enohpolyx", "pass", "email");
        USER_DAO.createUser(test);
        assertEquals(test.email(), USER_DAO.getUser(test.username()).email());
    }
}
