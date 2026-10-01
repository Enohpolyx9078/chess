package service;

import dataaccess.*;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    ClearService clearService = new ClearService();
    UserService service = new UserService();
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    @Test
    public void badUsernameTest() throws UnauthorizedException {
        clearService.clearApplication();
        assertThrows(
                UnauthorizedException.class,
                () -> service.login(new LoginRequest("NotReal", "ABC123")));
    }

    @Test
    public void badPasswordTest() throws UnauthorizedException, AlreadyTakenException {
        clearService.clearApplication();
        service.register(new UserData("enohpolyx", "ABC123", "a@example.com"));
        assertThrows(
                UnauthorizedException.class,
                () -> service.login(new LoginRequest("enohpolyx", "wrongo")));
    }

    @Test
    public void normalLoginTest() throws UnauthorizedException, AlreadyTakenException {
        clearService.clearApplication();
        UserData user = new UserData("mandycandy", "ABC123", "a@example.com");
        service.register(user);
        authDAO.clear(); // Simulate logging out
        LoginResult result = service.login(new LoginRequest(user.username(), user.password()));
        assertEquals(user.username(), result.username());
        assertNotNull(result.authToken());
    }
}
