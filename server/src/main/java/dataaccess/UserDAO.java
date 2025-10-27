package dataaccess;

import model.*;

public interface UserDAO {
    UserData createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUsers(String username) throws DataAccessException;
}
