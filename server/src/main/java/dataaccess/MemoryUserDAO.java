package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO {
    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteUsers(String username) throws DataAccessException {

    }
}
