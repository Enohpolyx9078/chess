package service;

import dataaccess.AlreadyTakenException;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {
    ClearService clearService = new ClearService();
    UserService userService = new UserService();

    @Test
    public void authExistsTest() throws AlreadyTakenException {
        clearService.clearApplication();
        UserData user = new UserData("name", "password", "email@example.com");
        String token = userService.register(user).authToken();
        assertTrue(AuthService.isAuthorized(token));
    }

    @Test
    public void authDoesNotExistTest() {
        clearService.clearApplication();
        assertFalse(AuthService.isAuthorized("BadToken"));
    }
}
