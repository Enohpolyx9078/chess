package service;

import dataaccess.*;
import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    @Test
    public void registerSuccess() throws DataAccessException {
        UserData register = new UserData("MandyCandy", "123ABC", "A@example.com");
        UserData resp = new UserData("MandyCandy", "123ABC", "a@example.com");
        UserDAO userAccess = new SQLUserDAO();
        UserService service = new UserService();
        service.register(register);
        assertEquals(resp.username(), userAccess.getUser(register.username()).username());
    }

    @Test
    public void usernameTakenTest() throws DataAccessException {
        UserData register = new UserData("oglee", "123", "a@example.com");
        UserService service = new UserService();
        service.register(register);
        assertThrows(AlreadyTakenException.class, () -> service.register(register));
    }
}
