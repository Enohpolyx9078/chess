package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.UnauthorizedException;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    ClearService clearService = new ClearService();
    UserService service = new UserService();
    AuthDAO authDAO = new MemoryAuthDAO();
    @Test
    public void badTokenTest() throws UnauthorizedException {
        clearService.clearApplication();
        assertThrows(
                UnauthorizedException.class,
                () -> service.logout("Bad Token"));
    }

    @Test
    public void goodTokenTest() throws UnauthorizedException, AlreadyTakenException {
        clearService.clearApplication();
        UserData user = new UserData("name", "password", "email@example.com");
        LoginResult result = service.register(user);
        assertEquals(1, authDAO.getSize());
        service.logout(result.authToken());
        assertEquals(0, authDAO.getSize());
    }
}
