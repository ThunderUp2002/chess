package service;

import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        userDAO = new DatabaseUserDAO();
        userService = new UserService(authDAO, userDAO);
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        authDAO.deleteAuths();
        userDAO.deleteUsers();
    }

    @Test
    public void registerSuccess() throws Exception {
        assertNotNull(userService.register(new RegisterRequest("username", "password", "email")));
    }

    @Test
    public void registerFailure() {
        assertThrows(BadRequestException.class, () -> userService.register(new RegisterRequest(null, null, null)));
    }

    @Test
    public void loginSuccess() throws Exception {
        userDAO.createUser(new UserData("username", "password", "email"));
        assertNotNull(userService.login(new LoginRequest("username", "password")));
    }

    @Test
    public void loginFailure() {
        assertThrows(BadRequestException.class, () -> userService.login(new LoginRequest(null, null)));
    }

    @Test
    public void logoutSuccess() throws Exception {
        RegisterResponse response = userService.register(new RegisterRequest("username", "password", "email"));
        String authToken = response.authToken();
        userService.logout(authToken);
        assertThrows(UnauthorizedException.class, () -> authDAO.getAuth(authToken));
    }

    @Test
    public void logoutFailure() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("abc"));
    }
}
