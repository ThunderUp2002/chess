package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new DatabaseUserDAO();
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        userDAO.deleteUsers();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        String username = "username";
        String password = "password";
        String email = "email@gmail.com";
        UserData userData = new UserData(username, password, email);
        UserData userDataTest = userDAO.createUser(userData);
        assertNotNull(userDataTest);
    }

    @Test
    public void createUserFailure() {
        assertThrows(NullPointerException.class, () -> userDAO.createUser(null));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        String username = "username";
        String password = "password";
        String email = "email@gmail.com";
        UserData userData = new UserData(username, password, email);
        UserData createdUser = userDAO.createUser(userData);
        String createdUsername = createdUser.username();
        assertNotNull(userDAO.getUser(createdUsername));
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        assertNull(userDAO.getUser(null));
    }

    @Test
    public void deleteUsersSuccess() throws DataAccessException {
        String username1 = "username1";
        String username2 = "username2";
        String username3 = "username3";

        String password1 = "password1";
        String password2 = "password2";
        String password3 = "password3";

        String email1 = "email1@gmail.com";
        String email2 = "email2@gmail.com";
        String email3 = "email3@gmail.com";

        UserData userData1 = new UserData(username1, password1, email1);
        UserData userData2 = new UserData(username2, password2, email2);
        UserData userData3 = new UserData(username3, password3, email3);

        userDAO.createUser(userData1);
        userDAO.createUser(userData2);
        userDAO.createUser(userData3);

        userDAO.deleteUsers();

        assertNull(userDAO.getUser(username1));
        assertNull(userDAO.getUser(username2));
        assertNull(userDAO.getUser(username3));
    }
}
