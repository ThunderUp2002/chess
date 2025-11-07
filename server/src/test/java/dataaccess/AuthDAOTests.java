package dataaccess;

import exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        authDAO.deleteAuths();
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        String username = "testUser";
        AuthData authData = authDAO.createAuth(username);
        assertNotNull(authData);
    }

    @Test
    public void createAuthFailure() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null);
        });
    }

    @Test
    public void getAuthSuccess() throws DataAccessException, UnauthorizedException {
        String username = "testUser";
        AuthData authData = authDAO.createAuth(username);
        String authToken = authData.authToken();
        AuthData authDataTest = authDAO.getAuth(authToken);
        assertNotNull(authDataTest);
    }

    @Test
    public void getAuthFailure() {
        assertThrows(UnauthorizedException.class, () -> {
            authDAO.getAuth(null);
        });
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException, UnauthorizedException {
        String username = "testUser";
        AuthData authData = authDAO.createAuth(username);
        String authToken = authData.authToken();
        authDAO.deleteAuth(authToken);
        assertThrows(UnauthorizedException.class, () -> {
            authDAO.getAuth(authToken);
        });
    }

    @Test
    public void deleteAuthFailure() {
        assertThrows(UnauthorizedException.class, () -> {
            authDAO.deleteAuth(null);
        });
    }

    @Test
    public void deleteAuthsSuccess() throws DataAccessException {
        String username1 = "username1";
        String username2 = "username2";
        String username3 = "username3";

        AuthData authData1 = authDAO.createAuth(username1);
        AuthData authData2 = authDAO.createAuth(username2);
        AuthData authData3 = authDAO.createAuth(username3);

        String authToken1 = authData1.authToken();
        String authToken2 = authData2.authToken();
        String authToken3 = authData3.authToken();

        authDAO.deleteAuths();

        assertThrows(UnauthorizedException.class, () -> {
            authDAO.getAuth(authToken1);
        });

        assertThrows(UnauthorizedException.class, () -> {
            authDAO.getAuth(authToken2);
        });

        assertThrows(UnauthorizedException.class, () -> {
            authDAO.getAuth(authToken3);
        });
    }
}
