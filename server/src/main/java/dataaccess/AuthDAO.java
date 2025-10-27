package dataaccess;

import model.*;

public interface AuthDAO {
    AuthData createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuths() throws DataAccessException;
}
