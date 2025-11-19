package ui;

import model.GameData;
import requests.CreateGameRequest;
import responses.CreateGameResponse;
import server.ServerFacade;

import java.util.Collection;
import java.util.Scanner;

public class PostLoginUI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void help() {
        System.out.println("List games: see a list of existing games");
        System.out.println("Create game: add a game to the games list");
        System.out.println("Play game: join an existing game as a player");
        System.out.println("Observe game: join an existing game as an observer");
        System.out.println("Logout: log out the current user");
        System.out.println("Help: see a list of possible actions");
    }

    public static Collection<GameData> listGames(ServerFacade facade, String authToken) {
        return null;
    }

    public static CreateGameResponse createGame(ServerFacade facade, String authToken) {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();
        try {
            return facade.createGame(new CreateGameRequest(gameName), authToken);
        } catch (Exception e) {
            System.out.println("Invalid game name");
            return null;
        }
    }

    public static void joinGame(ServerFacade facade, String authToken) {
        System.out.print("Enter game number: ");
        String gameNumber = scanner.nextLine();
        System.out.print("Enter desired color: ");
        String color = scanner.nextLine();
    }

    public static void logout(ServerFacade facade, String authToken) throws Exception {
        facade.logout(authToken);
    }
}
