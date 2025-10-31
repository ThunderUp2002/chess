package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.GeneralException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import java.util.Collection;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private static int gameID = 0;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws Exception {
        if (request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData gameData = new GameData(gameID++, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameID);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        return gameDAO.listGames();
    }

    public void joinGame(JoinGameRequest request, String authToken) throws Exception {
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        GameData gameData = gameDAO.getGame(gameID);

        if (gameData == null) {
            throw new GeneralException("Error: data missing");
        }

//        if (request.playerColor() != null) {
//            if ()
//        }
    }
}
