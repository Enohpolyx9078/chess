package java.service;

import dataaccess.AlreadyTakenException;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
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
