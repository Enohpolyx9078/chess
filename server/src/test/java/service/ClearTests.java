package service;

import dataaccess.*;
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

    @Test
    public void clearAuthTest() {
        UserData register = new UserData("enohpolyx", "123ABC", "a@example.com");
        AuthDAO authAccess = new MemoryAuthDAO();
        authAccess.createAuth(register);
        assertEquals(1, authAccess.getSize());
        authAccess.clear();
        assertEquals(0, authAccess.getSize());
    }
}
