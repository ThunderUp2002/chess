package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import requests.CreateGameRequest;
import responses.CreateGameResponse;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private static int gameID = 0;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws Exception {
        GameData gameData = new GameData(gameID++, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameID);
    }
}
