package dataaccess;

import exceptions.UnauthorizedException;
import model.*;
import java.util.Map;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    Map<String, AuthData> getAllAuths() throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException;

    void deleteAuths() throws DataAccessException;
}
