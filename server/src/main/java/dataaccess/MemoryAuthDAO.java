package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        authData = new AuthData(UUID.randomUUID().toString(), authData.username());

        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuths() throws DataAccessException {
        authTokens.clear();
    }
}
