package service;

import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.AlreadyTakenException;
import exceptions.GeneralException;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
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

    public LoginResponse login(LoginRequest request) throws Exception {
        UserData userData = userDAO.getUser(request.username());
        if (userData != null) {
            if (Objects.equals(request.password(), userData.password())) {
                AuthData authData = authDAO.createAuth(request.username());
                return new LoginResponse(request.username(), authData.authToken());
            }
        }
        throw new GeneralException("Error: something went wrong");
    }

    public void logout(String authToken) throws Exception {
        authDAO.deleteAuth(authToken);
    }
}
