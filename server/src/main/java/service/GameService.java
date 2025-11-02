package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
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
    private static int gameID = 1;

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
        int gameID = GameService.gameID;
        GameService.gameID++;
        GameData gameData = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameID);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return gameDAO.listGames();
    }

    public void joinGame(JoinGameRequest request, String authToken) throws Exception {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(request.gameID());
        if (gameData == null) {
            throw new BadRequestException("Error: bad request");
        }

        boolean isWhite = "white".equalsIgnoreCase(request.playerColor());
        boolean isBlack = "black".equalsIgnoreCase(request.playerColor());

        if (!isWhite && !isBlack) {
            throw new BadRequestException("Error: bad request");
        }

        if (isBlack && gameData.blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        if (isWhite && gameData.whiteUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        gameDAO.updateGame(request.playerColor(), request.gameID(), username);
    }
}
