package ui;

import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginUI {
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
            gameIDMap.clear();
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
            if (e.getMessage().contains("401")) {
                System.out.println("Unauthorized to perform this action");
            }
            else {
                System.out.println("Unable to list games");
            }
            System.out.print(RESET_TEXT_COLOR);
            return Collections.emptyList();
        }
    }

    public static void createGame(ServerFacade facade, String authToken, Scanner scanner) {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        if (gameName.isEmpty()) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Game name cannot be empty.");
            System.out.print(RESET_TEXT_COLOR);
            return;
        }
        try {
            facade.createGame(new CreateGameRequest(gameName), authToken);
            System.out.print(SET_TEXT_COLOR_GREEN);
            System.out.printf("\"%s\" created successfully%n", gameName);
            System.out.print(RESET_TEXT_COLOR);
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            if (e.getMessage().contains("400")) {
                System.out.println("Invalid request");
            }
            if (e.getMessage().contains("401")) {
                System.out.println("Unauthorized to perform this action");
            }
            else {
                System.out.println("Unable to create game");
            }
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    private static void displayGame(GameData gameData, String color) {
        try {
            boolean whiteView = color.equals("white");
            GameplayUI gameplayUI = new GameplayUI(gameData, whiteView);
            gameplayUI.run();
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error displaying game");
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    public static void playGame(ServerFacade facade, String authToken, String username, Scanner scanner) {
        try {
            Collection<GameData> gamesList = listGames(facade, authToken);
            List<GameData> gamesArray = new ArrayList<>(gamesList);
            System.out.print("Enter game number to play: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(scanner.nextLine().trim());
                if (gameNumber < 1 || gameNumber > gamesList.size()) {
                    System.out.print(SET_TEXT_COLOR_RED);
                    System.out.println("Game number not available");
                    System.out.print(RESET_TEXT_COLOR);
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Invalid number");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            if (!gameIDMap.containsKey(gameNumber)) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Could not find game number");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            GameData selectedGame = gamesArray.get(gameNumber - 1);
            if (selectedGame.blackUsername() != null && selectedGame.whiteUsername() != null) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("The selected game is full. You may observe this game or select another game with an empty slot.");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            System.out.print("Enter desired color (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toLowerCase();
            if (!color.equals("white") && !color.equals("black")) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Invalid color choice");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            if (username.equals(selectedGame.whiteUsername()) && color.equals("black")) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("You are already the white player in this game. Self-play is not allowed.");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            if (username.equals(selectedGame.blackUsername()) && color.equals("white")) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("You are already the black player in this game. Self-play is not allowed.");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            int gameID = gameIDMap.get(gameNumber);
            facade.joinGame(new JoinGameRequest(color, gameID), authToken);
            displayGame(selectedGame, color);
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            if (e.getMessage().contains("400")) {
                System.out.println("Invalid request");
            }
            if (e.getMessage().contains("401")) {
                System.out.println("Unauthorized to perform this action");
            }
            if (e.getMessage().contains("403")) {
                System.out.println("Slot already taken. Please try again.");
            }
            else {
                System.out.println("Unable to join game");
            }
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    public static void observeGame(ServerFacade facade, String authToken, Scanner scanner) {
        try {
            Collection<GameData> gamesList = listGames(facade, authToken);
            List<GameData> gamesArray = new ArrayList<>(gamesList);
            System.out.print("Enter game number to observe: ");
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(scanner.nextLine().trim());
                if (gameNumber < 1 || gameNumber > gamesList.size()) {
                    System.out.print(SET_TEXT_COLOR_RED);
                    System.out.println("Game number not available");
                    System.out.print(RESET_TEXT_COLOR);
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Invalid number");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            if (!gameIDMap.containsKey(gameNumber)) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Could not find game number");
                System.out.print(RESET_TEXT_COLOR);
                return;
            }
            GameData selectedGame = gamesArray.get(gameNumber - 1);
            int gameID = gameIDMap.get(gameNumber);
            displayGame(selectedGame,"white");
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Unable to observe game");
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    public static void logout(ServerFacade facade, String authToken) throws Exception {
        facade.logout(authToken);
    }
}
