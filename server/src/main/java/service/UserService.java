package service;

import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.AlreadyTakenException;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;
import model.UserData;
import model.AuthData;

import java.util.Objects;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest request) throws Exception {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData userData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(userData);
        AuthData authData = authDAO.createAuth(request.username());
        return new RegisterResponse(request.username(), authData.authToken());
    }

    public LoginResponse login(LoginRequest request) throws GeneralException, BadRequestException, UnauthorizedException, DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData userData = userDAO.getUser(request.username());
        if (userData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (!Objects.equals(userData.password(), request.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authData = authDAO.createAuth(request.username());
        return new LoginResponse(request.username(), authData.authToken());
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        authDAO.deleteAuth(authToken);
    }
}
