package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthUtilityTests {
    ClearService clearService = new ClearService();
    UserService userService = new UserService();

    @Test
    public void authExistsTest() throws DataAccessException {
        clearService.clearApplication();
        UserData user = new UserData("name", "password", "email@example.com");
        String token = userService.register(user).authToken();
        assertTrue(AuthUtility.isAuthorized(token));
    }

    @Test
    public void authDoesNotExistTest() throws DataAccessException {
        clearService.clearApplication();
        assertFalse(AuthUtility.isAuthorized("BadToken"));
    }

    @Test
    public void instantiationTest() {
        assertThrows(UnsupportedOperationException.class,
                AuthUtility::new);
    }
}
