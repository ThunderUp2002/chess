package ui;

import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, Integer> gameIDMap = new HashMap<>();

    public static void help() {
        System.out.println();
        System.out.print(SET_TEXT_COLOR_BLUE + "CREATE:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" add a game to the games list");
        System.out.print(SET_TEXT_COLOR_BLUE + "PLAY:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" join an existing game as a player");
        System.out.print(SET_TEXT_COLOR_BLUE + "OBSERVE:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" join an existing game as an observer");
        System.out.print(SET_TEXT_COLOR_BLUE + "LIST:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" see a list of existing games");
        System.out.print(SET_TEXT_COLOR_BLUE + "HELP:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" see a list of possible actions");
        System.out.print(SET_TEXT_COLOR_BLUE + "LOGOUT:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" log out the current user");
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
                System.out.printf("%d. \"%s\" | %s (WHITE) vs. %s (BLACK)%n",
                        counter,
                        game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "<EMPTY SLOT>",
                        game.blackUsername() != null ? game.blackUsername() : "<EMPTY SLOT>");
                gameIDMap.put(counter, game.gameID());
                counter++;
            }
            return gamesList;
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Unable to list games");
            System.out.print(RESET_TEXT_COLOR);
            return Collections.emptyList();
        }
    }

    public static void createGame(ServerFacade facade, String authToken) {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        if (gameName.isEmpty()) {
            System.out.println("Game name cannot be empty.");
            return;
        }
        try {
            facade.createGame(new CreateGameRequest(gameName), authToken);
            System.out.print(SET_TEXT_COLOR_GREEN);
            System.out.printf("\"%s\" created successfully%n", gameName);
            System.out.print(RESET_TEXT_COLOR);
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Unable to create game");
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print("Enter game number to observe: ");
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
            System.out.println(e.getMessage());
        }
    }

    public static void logout(ServerFacade facade, String authToken) throws Exception {
        facade.logout(authToken);
    }

    private static boolean isGameFull(GameData game) {
        return game.whiteUsername() != null && game.blackUsername() != null;
    }
}
