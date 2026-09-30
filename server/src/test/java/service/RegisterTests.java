package java.service;

import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    @Test
    public void registerSuccess() {
        UserData register = new UserData("mandycandy", "123", "a@example.com");
        UserService service = new UserService();
        UserDAO dataAccess = new MemoryUserDAO();
        service.register(register);
        assertEquals(register, dataAccess.getUser(register.username()));
    }

    @Test
    public void usernameTakenTest() {
        UserData register = new UserData("enohpolyx", "123", "a@example.com");
        UserService service = new UserService();
        service.register(register);
        assertThrows(AlreadyTakenException.class, () -> service.register(register));
    }

    @Test
    public void caseInsensitiveTest() {
        UserData register = new UserData("enohpolyx", "123", "a@example.com");
        UserData register2 = new UserData("ENOHPOLYX", "123", "a@example.com");
        UserService service = new UserService();
        service.register(register);
        assertThrows(AlreadyTakenException.class, () -> service.register(register2));
    }
}
