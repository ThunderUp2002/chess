package dataaccess;

import exceptions.UnauthorizedException;
import model.AuthData;

import java.util.Map;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public Map<String, AuthData> getAllAuths() throws DataAccessException {
        return Map.of();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {

    }

    @Override
    public void deleteAuths() throws DataAccessException {

    }
}
