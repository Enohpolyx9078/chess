package service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {
    @Test
    public void clearUsersTest() throws AlreadyTakenException {
        UserData register = new UserData("MandyCandy", "123ABC", "A@example.com");
        UserDAO userAccess = new MemoryUserDAO();
        UserService service = new UserService();
        service.register(register);
        assertEquals(1, userAccess.getSize());
        userAccess.clear();
        assertEquals(0, userAccess.getSize());
    }
}
