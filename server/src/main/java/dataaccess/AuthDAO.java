package dataaccess;

import exceptions.UnauthorizedException;
import model.*;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException;

    void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException;

    void deleteAuths() throws DataAccessException;
}
