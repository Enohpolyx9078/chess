package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    @Test
    public void registerSuccess() throws AlreadyTakenException {
        UserData register = new UserData("MandyCandy", "123ABC", "A@example.com");
        UserData resp = new UserData("mandycandy", "123ABC", "a@example.com");
        UserDAO userAccess = new MemoryUserDAO();
        UserService service = new UserService();
        service.register(register);
        assertEquals(resp, userAccess.getUser(register.username()));
    }

    @Test
    public void usernameTakenTest() throws AlreadyTakenException {
        UserData register = new UserData("oglee", "123", "a@example.com");
        UserService service = new UserService();
        service.register(register);
        assertThrows(AlreadyTakenException.class, () -> service.register(register));
    }

    @Test
    public void caseInsensitiveTest() throws AlreadyTakenException {
        UserData register = new UserData("enohpolyx", "123", "a@example.com");
        UserData register2 = new UserData("ENOHPOLYX", "123", "a@example.com");
        UserService service = new UserService();
        service.register(register);
        assertThrows(AlreadyTakenException.class, () -> service.register(register2));
    }
}
