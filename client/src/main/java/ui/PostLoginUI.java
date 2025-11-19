package ui;

import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import server.ServerFacade;

import java.util.*;

public class PostLoginUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, Integer> gameIDMap = new HashMap<>();

    public static void help() {
        System.out.println("List games: see a list of existing games");
        System.out.println("Create game: add a game to the games list");
        System.out.println("Play game: join an existing game as a player");
        System.out.println("Observe game: join an existing game as an observer");
        System.out.println("Logout: log out the current user");
        System.out.println("Help: see a list of possible actions");
    }

    public static Collection<GameData> listGames(ServerFacade facade, String authToken) {
        try {
            Collection<GameData> gamesList = facade.listGames(authToken);
            if (gamesList.isEmpty()) {
                System.out.println("There are currently no active games.");
                return Collections.emptyList();
            }

            int counter = 1;
            for (GameData game : gamesList) {
                System.out.printf("%d. Game: \"%s\" Players: %s (white) vs. %s (black)%n",
                        counter,
                        game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "empty",
                        game.blackUsername() != null ? game.blackUsername() : "empty");
                gameIDMap.put(counter, game.gameID());
                counter++;
            }
            return gamesList;
        } catch (Exception e) {
            System.out.println("Unable to list games");
            return Collections.emptyList();
        }
    }

    public static CreateGameResponse createGame(ServerFacade facade, String authToken) {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        if (gameName.isEmpty()) {
            System.out.print("Game name cannot be empty. Please try again.");
            return null;
        }
        try {
            return facade.createGame(new CreateGameRequest(gameName), authToken);
        } catch (Exception e) {
            System.out.println("Unable to create game");
            return null;
        }
    }

    public static void playGame(ServerFacade facade, String authToken) {
        try {
            Collection<GameData> gamesList = listGames(facade, authToken);
            System.out.print("Enter game number to play: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(scanner.nextLine().trim());
                if (gameNumber < 1 || gameNumber > gamesList.size()) {
                    System.out.println("Game number not available");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
                return;
            }
            if (!gameIDMap.containsKey(gameNumber)) {
                System.out.println("Could not find game number");
                return;
            }
            System.out.print("Enter desired color (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toLowerCase();
            if (!color.equals("white") && !color.equals("black")) {
                System.out.println("Invalid color choice");
                return;
            }
            int gameID = gameIDMap.get(gameNumber);
            facade.joinGame(new JoinGameRequest(color, gameID), authToken);
        } catch (Exception e) {
            System.out.println("Unable to join game");
        }
    }

    public static void observeGame(ServerFacade facade, String authToken) {
        try {
            Collection<GameData> gamesList = listGames(facade, authToken);
            System.out.println("Enter game number to observe: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(scanner.nextLine().trim());
                if (gameNumber < 1 || gameNumber > gamesList.size()) {
                    System.out.println("Game number not available");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
                return;
            }
            if (!gameIDMap.containsKey(gameNumber)) {
                System.out.println("Could not find game number");
                return;
            }
            int gameID = gameIDMap.get(gameNumber);
            facade.joinGame(new JoinGameRequest(null, gameID), authToken);
        } catch (Exception e) {
            System.out.println("Unable to join game");
        }
    }

    public static void logout(ServerFacade facade, String authToken) throws Exception {
        facade.logout(authToken);
    }

    private static boolean isGameFull(GameData game) {
        return game.whiteUsername() != null && game.blackUsername() != null;
    }
}
